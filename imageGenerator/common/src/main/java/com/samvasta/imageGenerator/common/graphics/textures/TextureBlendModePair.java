package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.images.BlendMode;
import org.jetbrains.annotations.NotNull;

public class TextureBlendModePair
{
    public final ITexture texture;
    public final BlendMode blendMode;

    public TextureBlendModePair(@NotNull final ITexture texture, final BlendMode blendMode){
        if(texture == null){
            throw new IllegalArgumentException("texture cannot be null");
        }
        this.texture = texture;
        this.blendMode = blendMode;
    }
}
