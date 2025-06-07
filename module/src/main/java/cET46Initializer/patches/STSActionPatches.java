package cET46Initializer.patches;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 各种原生 STS 行动的补丁, 主要是针对输入为 0 的特殊情况
 */
public class STSActionPatches {

    /**
     * 所有能力层数 0 的补丁, 如果不打对实际游戏无影响但是会有能力图标
     */

    /**
     * 猎宝弃牌数量为 0 的补丁, 主要针对 Prepared 这张牌
     */

    public static boolean underPackage(Class<?> clazz, String package_) {
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            return package_.isEmpty();
        }
        return pkg.getName().equals(package_) || pkg.getName().startsWith(package_ + ".");
    }
}
