package gui.components.structurepanel;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;

public class BiomeUnit extends JPanel {

    private final JTextField xCord;
    private final JTextField zCord;
    private final JComboBox<String> biomeSelector;

    public BiomeUnit() {
        this.xCord = new JTextField();
        this.zCord = new JTextField();
        this.biomeSelector = new JComboBox<>(Biome.REGISTRY.values().stream().map(Biome::getName).toArray(String[]::new));

        PromptSupport.setPrompt("X", this.xCord);
        PromptSupport.setPrompt("Z", this.zCord);
        this.biomeSelector.setPreferredSize(new Dimension(150, 25));

        this.add(this.xCord);
        this.add(this.zCord);
        this.add(this.biomeSelector);
    }

    public boolean matches(OverworldBiomeSource biomeSource) {
        try {
            return biomeSource.getBiome(Integer.parseInt(this.xCord.getText().trim()), 0, Integer.parseInt(this.zCord.getText()))
                    .getName() == this.biomeSelector.getSelectedItem();
        } catch (NumberFormatException exception) {
            return true;
        }
    }
}
