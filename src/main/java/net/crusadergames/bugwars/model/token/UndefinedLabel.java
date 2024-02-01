package net.crusadergames.bugwars.model.token;

import lombok.Data;

@Data
public class UndefinedLabel {

    public String undefinedLabel;

    public int locationInBytecode;

    public UndefinedLabel(String undefinedLabel, int locationInBytecode) {
        this.undefinedLabel = undefinedLabel;
        this.locationInBytecode = locationInBytecode;
    }
}
