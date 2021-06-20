package com.kwpugh.more_gems.items.baseclasses;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kwpugh.more_gems.init.TagInit;
import com.kwpugh.more_gems.items.areatools.ObsidianBreaking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ModPaxel extends MiningToolItem
{	
	static 
	{
		AXE_BLOCKS = Sets.newHashSet(new Material[]{Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.GOURD});

		PATH_BLOCKSTATES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getDefaultState()));
      
		STRIPPED_BLOCKS = (new Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).build();
	}
	   
	private static final Set<Material> AXE_BLOCKS;
	private static final Map<Block, Block> STRIPPED_BLOCKS;
	
	//private static final Set<Block> EFFECTIVE_BLOCKS;
	private static final Map<Block, BlockState> PATH_BLOCKSTATES;

	public ModPaxel(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) 
	{
		super(attackDamage, attackSpeed, material, TagInit.PAXEL_ADDITIONS, settings);
	}

	@Override
	public boolean isSuitableFor(BlockState state) // Mining level checks for pickaxe functionality
	{
		int i = this.getMaterial().getMiningLevel();
		
		if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.CRYING_OBSIDIAN) && !state.isOf(Blocks.NETHERITE_BLOCK) && !state.isOf(Blocks.RESPAWN_ANCHOR) && !state.isOf(Blocks.ANCIENT_DEBRIS))
		{
			if (!state.isOf(Blocks.DIAMOND_BLOCK) && !state.isOf(Blocks.DIAMOND_ORE) && !state.isOf(Blocks.EMERALD_ORE) && !state.isOf(Blocks.EMERALD_BLOCK) && !state.isOf(Blocks.GOLD_BLOCK) && !state.isOf(Blocks.GOLD_ORE) && !state.isOf(Blocks.REDSTONE_ORE))
			{
				if (!state.isOf(Blocks.IRON_BLOCK) && !state.isOf(Blocks.IRON_ORE) && !state.isOf(Blocks.LAPIS_BLOCK) && !state.isOf(Blocks.LAPIS_ORE)) 
				{
					Material material = state.getMaterial();
	        
					return material == Material.STONE || material == Material.METAL || material == Material.REPAIR_STATION || state.isOf(Blocks.NETHER_GOLD_ORE);
	            } 
				else 
				{
	               return i >= 1;
	            }
			} 
			else 
			{
	            return i >= 2;
	        }
		} 
		else
		{
			return i >= 3;
		}
	}
	   
	public ActionResult useOnBlock(ItemUsageContext context) 
	{
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = (Block)STRIPPED_BLOCKS.get(blockState.getBlock());
      
		if(block == null) // Shovel logic
		{
			if (context.getSide() == Direction.DOWN)
			{
				return ActionResult.PASS;
			}
			else 
			{
				PlayerEntity playerEntity = context.getPlayer();
				BlockState blockState2 = (BlockState)PATH_BLOCKSTATES.get(blockState.getBlock());
				BlockState blockState3 = null;
        
				if (blockState2 != null && world.getBlockState(blockPos.up()).isAir())
				{
					world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
					blockState3 = blockState2;
				} 
				else if (blockState.getBlock() instanceof CampfireBlock && (Boolean)blockState.get(CampfireBlock.LIT)) 
				{
					if (!world.isClient())
					{
						world.syncWorldEvent((PlayerEntity)null, 1009, blockPos, 0);
					}

					CampfireBlock.extinguish(playerEntity, world, blockPos, blockState);
					blockState3 = (BlockState)blockState.with(CampfireBlock.LIT, false);
				}

				if (blockState3 != null)
				{
					if (!world.isClient)
					{
						world.setBlockState(blockPos, blockState3, 11);
				
						if (playerEntity != null)
						{
							context.getStack().damage(1, (LivingEntity)playerEntity, (Consumer)((p) -> {
								playerEntity.sendToolBreakStatus(context.getHand());
								}));
						}
					}

					return ActionResult.success(world.isClient);
				} 
				else 
				{
					return ActionResult.PASS;
				}
			}  	  
		}
		else
		{
			if(block != null) // Axe stripping logic
			{   
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
	    
				if (!world.isClient)
				{
					world.setBlockState(blockPos, (BlockState)block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
	            
					if (playerEntity != null)
					{
						context.getStack().damage(1, (LivingEntity)playerEntity, (Consumer)((p) -> {
							playerEntity.sendToolBreakStatus(context.getHand());
							}));
					}
				}

				return ActionResult.success(world.isClient);
			}
			else
			{
				return ActionResult.PASS;
			}
    	  
		}
	}

	// Needed to override mining speed if Faster Obsidian enchant is present
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) 
	{
		Material material = state.getMaterial();
		
		if(ObsidianBreaking.fastAtObsidian(state, stack))
		{
			return ObsidianBreaking.fastObsidianSpeed();
		}
		else if(AXE_BLOCKS.contains(material) || material == Material.METAL || material == Material.REPAIR_STATION || material == Material.STONE)
		{
			return this.miningSpeed;
		}
	
		return material != Material.METAL && material != Material.REPAIR_STATION && material != Material.STONE ? super.getMiningSpeedMultiplier(stack, state) : this.miningSpeed;
		
	}
}


