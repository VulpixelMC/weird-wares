package gay.sylv.weird_wares.impl.block;

import com.mojang.serialization.MapCodec;
import gay.sylv.weird_wares.impl.LootTables;
import gay.sylv.weird_wares.impl.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

import static gay.sylv.weird_wares.impl.util.BlockFlags.DEFAULT;
import static gay.sylv.weird_wares.impl.util.BlockFlags.STATE_CHANGE;
import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public class NetherReactorBlock extends BaseEntityBlock {
	public static final MapCodec<NetherReactorBlock> CODEC = simpleCodec(NetherReactorBlock::new);
	public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
	
	private static final Component ACTIVE = Component.translatable("chat.weird-wares.nether_reactor.active");
	private static final Component INCORRECT_PATTERN = Component.translatable("chat.weird-wares.nether_reactor.incorrect_pattern");
	private static final Direction.Axis[] GOLD_AXES = new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z};
	private static final Block MEAT = net.minecraft.world.level.block.Blocks.COBBLESTONE;
	private static final Block GEM = net.minecraft.world.level.block.Blocks.GOLD_BLOCK;
	private static final Block REACTING_WASTE = Blocks.GLOWING_OBSIDIAN.block();
	private static final Block WASTE = net.minecraft.world.level.block.Blocks.OBSIDIAN;
	private static final double MIN_DIST = new BlockPos(0, 0, 0).distSqr(new BlockPos(1, 1, 1));
	
	public NetherReactorBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(STATE, State.INACTIVE));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}
	
	@Override
	protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NetherReactorBlockEntity(pos, state);
	}
	
	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (state.getValue(STATE) != State.ACTIVE) {
			return super.getDestroyProgress(state, player, level, pos);
		} else {
			return 0.0f;
		}
	}
	
	@Override
	protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return useItemOn(ItemStack.EMPTY, state, level, pos, player, InteractionHand.MAIN_HAND, hitResult).result();
	}
	
	@Override
	protected @NotNull ItemInteractionResult useItemOn(
			ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
	) {
		if (level.isClientSide() || state.getValue(STATE) != State.INACTIVE) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
		
		boolean correctPattern = checkPattern(level, pos);
		PlayerList playerList = Objects.requireNonNull(level.getServer()).getPlayerList();
		if (!correctPattern) {
			playerList.broadcastSystemMessage(INCORRECT_PATTERN, true);
			return ItemInteractionResult.FAIL;
		} else {
			playerList.broadcastSystemMessage(ACTIVE, true);
			level.setBlock(pos, state.setValue(STATE, State.ACTIVE), STATE_CHANGE);
			NetherReactorBlockEntity.placeSpire(level, pos);
			return ItemInteractionResult.SUCCESS;
		}
	}
	
	@Override
	protected @NotNull RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return createTickerHelper(blockEntityType, Blocks.NETHER_REACTOR.type(), NetherReactorBlockEntity::tick);
	}
	
	private static boolean checkPattern(Level level, BlockPos pos) {
		BlockPos upPos = pos.above();
		BlockPos downPos = pos.below();
		boolean ret;
		ret = level.getBlockState(upPos).is(MEAT) &&
				level.getBlockState(downPos).is(MEAT);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			ret = ret &&
					level.getBlockState(upPos.relative(direction)).is(MEAT) &&
					level.getBlockState(downPos.relative(direction)).is(MEAT) &&
					level.getBlockState(downPos.relative(direction.getClockWise()).relative(direction)).is(GEM) &&
					level.getBlockState(pos.relative(direction.getClockWise()).relative(direction)).is(MEAT);
		}
		return ret;
	}
	
	@org.jetbrains.annotations.ApiStatus.Internal
	public static class NetherReactorBlockEntity extends BlockEntity {
		private int progressTimer = 0;
		private Progress progress = Progress.INITIAL;
		
		NetherReactorBlockEntity(BlockPos pos, BlockState blockState) {
			super(Blocks.NETHER_REACTOR.type(), pos, blockState);
		}
		
		static void tick(Level level, BlockPos pos, BlockState state, NetherReactorBlockEntity blockEntity) {
			if (level.isClientSide() || state.getValue(STATE) != State.ACTIVE) return;
			if (blockEntity.progress != Progress.END) {
				blockEntity.progressTimer++;
				
				if (blockEntity.progressTimer % 8 == 0 && level instanceof ServerLevel serverLevel) {
					LootParams lootParams = new LootParams.Builder(serverLevel)
							.create(LootContextParamSets.EMPTY);
					LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(LootTables.NETHER_REACTION_BYPRODUCT);
					lootTable.getRandomItems(lootParams, serverLevel.random)
							.forEach(stack -> {
								Vec3 randomPos = randomPos(pos, serverLevel.random);
								var itemEntity = new ItemEntity(serverLevel, randomPos.x(), randomPos.y(), randomPos.z(), stack);
								itemEntity.setDefaultPickUpDelay();
								serverLevel.addFreshEntity(itemEntity);
							});
				}
				
				if (blockEntity.progressTimer > blockEntity.progress.lifetime()) {
					blockEntity.progressTimer = 0;
					blockEntity.progress = blockEntity.progress.next();
					
					boolean hasSpawned = false;
					for (int i = 0; i < 4; i++) {
						boolean willSpawn = level.random.nextBoolean();
						if (!hasSpawned && i == 3) {
							willSpawn = true; // spawn no matter what
						}
						
						if (willSpawn && blockEntity.progress.ordinal() >= Progress.MID1.ordinal() && blockEntity.progress.ordinal() < Progress.FINISH1.ordinal()) {
							hasSpawned = true;
							ZombifiedPiglin zombifiedPiglin = EntityType.ZOMBIFIED_PIGLIN.create(level);
							Vec3 mobPos = randomPos(pos, level.random);
							assert zombifiedPiglin != null;
							zombifiedPiglin.setPos(mobPos);
							level.addFreshEntity(zombifiedPiglin);
						}
					}
					
					BlockPos upPos = pos.above();
					BlockPos downPos = pos.below();
					switch (blockEntity.progress) {
						case BEGIN1 -> {
							setReactingWaste(level, upPos);
							for (Direction direction : Direction.Plane.HORIZONTAL) {
								setReactingWaste(level, upPos.relative(direction));
								setReactingWaste(level, upPos.relative(direction.getClockWise()).relative(direction));
							}
						}
						case BEGIN2 -> {
							for (Direction direction : Direction.Plane.HORIZONTAL) {
								setReactingWaste(level, pos.relative(direction.getClockWise()).relative(direction));
							}
						}
						case BEGIN3 -> {
							setReactingWaste(level, downPos);
							for (Direction direction : Direction.Plane.HORIZONTAL) {
								setReactingWaste(level, downPos.relative(direction));
								setReactingWaste(level, downPos.relative(direction.getClockWise()).relative(direction));
							}
						}
						case FINISH1 -> {
							for (BlockPos blockPos : BlockPos.betweenClosed(upPos.west().south(), upPos.east().north())) {
								setWaste(level, blockPos);
							}
						}
						case FINISH2 -> {
							for (BlockPos blockPos : BlockPos.betweenClosed(pos.west().south(), pos.east().north())) {
								if (blockPos.equals(pos)) continue;
								setWaste(level, blockPos);
							}
						}
						case FINISH3 -> {
							for (BlockPos blockPos : BlockPos.betweenClosed(downPos.west().south(), downPos.east().north())) {
								setWaste(level, blockPos);
							}
						}
					}
				}
			} else {
				level.setBlock(pos, state.setValue(STATE, State.USED), STATE_CHANGE);
				destroySpire(level, pos);
			}
		}
		
		private static void setReactingWaste(Level level, BlockPos pos) {
			level.setBlock(pos, REACTING_WASTE.defaultBlockState(), STATE_CHANGE);
		}
		
		private static void setWaste(Level level, BlockPos pos) {
			level.setBlock(pos, WASTE.defaultBlockState(), STATE_CHANGE);
		}
		
		private static void placeSpire(Level level, BlockPos pos) {
			if (level instanceof ServerLevel serverLevel) {
				StructureTemplateManager structureManager = Objects.requireNonNull(level.getServer()).getStructureManager();
				StructureTemplate structureTemplate = structureManager.get(modId("nether_reactor/spire")).orElseThrow();
				if (!structureTemplate.placeInWorld(serverLevel, pos.offset(-8, -2, -8), BlockPos.ZERO, new StructurePlaceSettings(), serverLevel.random, DEFAULT)) {
					Main.LOGGER.error("Failed to place nether spire!");
				}
			}
		}
		
		private static void destroySpire(Level level, BlockPos pos) {
			if (level instanceof ServerLevel serverLevel) {
				StructureTemplateManager structureManager = Objects.requireNonNull(level.getServer()).getStructureManager();
				StructureTemplate structureTemplate = structureManager.get(modId("nether_reactor/empty_spire")).orElseThrow();
				if (!structureTemplate.placeInWorld(serverLevel, pos.offset(-8, -1, -8), BlockPos.ZERO, new StructurePlaceSettings(), serverLevel.random, DEFAULT)) {
					Main.LOGGER.error("Failed to place empty nether spire!");
				}
			}
		}
		
		@NotNull
		private static Vec3 randomPos(BlockPos center, RandomSource randomSource) {
			Vec3 pos = new Vec3(randomSource.nextInt(center.getX() - 6, center.getX() + 6) + randomSource.nextDouble() / 2.0, center.getY(), randomSource.nextInt(center.getZ() - 6, center.getZ() + 6) + randomSource.nextDouble() / 2.0);
			if (pos.distanceToSqr(Vec3.atCenterOf(center)) > MIN_DIST) {
				return pos;
			} else {
				return randomPos(center, randomSource); // FIXME: why does this need recursion
			}
		}
		
		@Override
		protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
			var modTag = new CompoundTag();
			modTag.putInt("progress_timer", progressTimer);
			
			tag.put("weird-wares", modTag);
		}
		
		@Override
		protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
			CompoundTag modTag = tag.getCompound("weird-wares");
			progressTimer = modTag.getInt("progress_timer");
		}
		
		@Nullable
		@Override
		public Packet<ClientGamePacketListener> getUpdatePacket() {
			return ClientboundBlockEntityDataPacket.create(this);
		}
		
		@Override
		public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
			return saveWithoutMetadata(registries);
		}
	}
	
	public enum State implements StringRepresentable {
		INACTIVE,
		ACTIVE,
		USED;
		
		@Override
		public @NotNull String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
	
	public enum Progress implements StringRepresentable {
		INITIAL(20),
		BEGIN1(20),
		BEGIN2(20),
		BEGIN3(20),
		MID1(225),
		MID2(225),
		MID3(225),
		MID4(225),
		FINISH1(20),
		FINISH2(20),
		FINISH3(20),
		END(0);
		
		private final int lifetime;
		
		Progress(int lifetime) {
			this.lifetime = lifetime;
		}
		
		@Override
		public @NotNull String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
		
		public int lifetime() {
			return lifetime;
		}
		
		public Progress next() {
			return Progress.values()[this.ordinal() + 1];
		}
	}
}
