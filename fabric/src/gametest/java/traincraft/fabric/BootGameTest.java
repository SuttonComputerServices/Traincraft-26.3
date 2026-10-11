package traincraft.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class BootGameTest {
	@GameTest
	public void bootsEmptyMod(GameTestHelper helper) {
		helper.succeed();
	}
}
