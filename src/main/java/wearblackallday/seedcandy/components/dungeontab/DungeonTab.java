package wearblackallday.seedcandy.components.dungeontab;

import com.formdev.flatlaf.util.StringUtils;
import com.seedfinding.mcbiome.biome.Biome;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.TextBox;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.seedcandy.util.Dungeon;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class DungeonTab extends AbstractTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final TextBox dungeonOutput = new TextBox(false);
	private final JTextField floorString = new JTextField();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final JLabel bitLabel = new JLabel();
	private final LPanel userEntry = this.buildUserEntry();

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.floorString.setFont(this.floorString.getFont().deriveFont(16F));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);

		Toolkit.getDefaultToolkit().addAWTEventListener(e -> this.updateInfo(), AWTEvent.MOUSE_EVENT_MASK);
		this.sizeSelector.addActionListener(e -> this.floorPanel.setFloor(this.sizeSelector.getSelected()));

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), this.userEntry, this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);
		this.updateInfo();
	}

	private void updateInfo() {
		var info = this.floorPanel.getInfo();
		this.bitLabel.setText("Bits: " + info.bits());
		this.floorString.setText(info.floor());
	}

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(new Biome(null, null, -1, "other Biome", null,
			null, Float.NaN, Float.NaN, Float.NaN, null, null));
		biomes.sort(Comparator.comparingInt(Biome::getId));
		return biomes;
	}

	private LPanel buildUserEntry() {
		return new LPanel()
			.addComponent(this.sizeSelector)
			.addTextField("X", "x")
			.addTextField("Y", "y")
			.addTextField("Z", "z")
			.addComponent(this.biomeSelector)
			.addButton("crack", (panel, button, event) -> {
				this.dungeonOutput.setText("");
				this.parseDungeon().crack().forEach(this.dungeonOutput::addEntry);
				if(this.getOutput().isEmpty()) this.dungeonOutput.setText("no results");
			})
			.addButton("crack to file", (panel, button, event) -> {
				JFileChooser chooser = new JFileChooser();
				int selection = chooser.showSaveDialog(this);
				if(selection == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						file.delete();
						file.createNewFile();
						FileWriter writer = new FileWriter(file);
						List<Long> crack = this.parseDungeon().crack();
						for(Long seed : crack) {
							writer.write(seed.toString() + "\n");
						}
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			})
			.addComponent(this.bitLabel);
	}

	private Dungeon parseDungeon() {
		return new Dungeon(
			this.sizeSelector.getSelected(),
			this.floorString.getText(),
			this.userEntry.getInt("x"),
			this.userEntry.getInt("y"),
			this.userEntry.getInt("z"),
			this.getVersion(),
			this.biomeSelector.getSelected()
		);
	}

	@Override
	public String getOutput() {
		return this.dungeonOutput.getText();
	}
}
