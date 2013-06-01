package micdoodle8.mods.galacticraft.mars.wgen;

import java.util.Random;
import micdoodle8.mods.galacticraft.core.wgen.GCCoreWorldGenMinableMeta;
import micdoodle8.mods.galacticraft.mars.blocks.GCMarsBlocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Copyright 2012-2013, micdoodle8
 * 
 * All rights reserved.
 * 
 */
public class GCMarsBiomeDecorator
{
    protected World currentWorld;

    protected Random randomGenerator;

    protected int chunk_X;

    protected int chunk_Z;

    protected BiomeGenBase biome;

    protected WorldGenerator dirtGen;

    protected WorldGenerator deshGen;

    protected WorldGenerator tinGen;

    protected WorldGenerator copperGen;

    public GCMarsBiomeDecorator(BiomeGenBase par1BiomeGenBase)
    {
        this.copperGen = new GCCoreWorldGenMinableMeta(GCMarsBlocks.marsBlock.blockID, 4, 0, true, GCMarsBlocks.marsBlock.blockID, 3);
        this.tinGen = new GCCoreWorldGenMinableMeta(GCMarsBlocks.marsBlock.blockID, 4, 1, true, GCMarsBlocks.marsBlock.blockID, 3);
        this.deshGen = new GCCoreWorldGenMinableMeta(GCMarsBlocks.marsBlock.blockID, 4, 2, true, GCMarsBlocks.marsBlock.blockID, 3);
        this.dirtGen = new GCCoreWorldGenMinableMeta(GCMarsBlocks.marsBlock.blockID, 32, 6, true, GCMarsBlocks.marsBlock.blockID, 3);
        this.biome = par1BiomeGenBase;
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        if (this.currentWorld != null)
        {
            throw new RuntimeException("Already decorating!!");
        }
        else
        {
            this.currentWorld = par1World;
            this.randomGenerator = par2Random;
            this.chunk_X = par3;
            this.chunk_Z = par4;
            this.generateOres();
            this.currentWorld = null;
            this.randomGenerator = null;
        }
    }

    protected void genStandardOre1(int par1, WorldGenerator par2WorldGenerator, int par3, int par4)
    {
        for (int var5 = 0; var5 < par1; ++var5)
        {
            final int var6 = this.chunk_X + this.randomGenerator.nextInt(16);
            final int var7 = this.randomGenerator.nextInt(par4 - par3) + par3;
            final int var8 = this.chunk_Z + this.randomGenerator.nextInt(16);
            par2WorldGenerator.generate(this.currentWorld, this.randomGenerator, var6, var7, var8);
        }
    }

    protected void generateOres()
    {
        this.genStandardOre1(20, this.dirtGen, 0, 200);
        this.genStandardOre1(15, this.deshGen, 0, 128);
        this.genStandardOre1(26, this.copperGen, 0, 60);
        this.genStandardOre1(23, this.tinGen, 0, 60);
    }
}