package wearblackallday.components.dungeontab;

import com.seedfinding.mcbiome.biome.Biome;
import wearblackallday.components.AbstractTab;
import wearblackallday.components.TextBox;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Dungeon;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DungeonTab extends AbstractTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final JTextField floorString = new JTextField();
	private final JLabel bitLabel = new JLabel();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	private final TextBox dungeonOutput = new TextBox(false);
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final LPanel userEntry = this.buildUserEntry();

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.floorString.setFont(this.floorString.getFont().deriveFont(16F));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);

		this.sizeSelector.addActionListener(e -> {
			this.floorPanel.setFloor(this.sizeSelector.getSelected());
			this.updateInfo();
		});

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), this.userEntry, this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);
		this.updateInfo();
	}

	protected void updateInfo() {
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
