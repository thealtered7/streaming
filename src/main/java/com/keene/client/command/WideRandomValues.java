package com.keene.client.command;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import com.keene.streaming.core.models.Wide;

/**
 * Shared random value helpers for wide generate/mutate commands.
 * Column types follow the fixed a-z cycle: INTEGER / DOUBLE / VARCHAR.
 */
final class WideRandomValues {

    private static final String LOREM_IPSUM[] = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean a fringilla eros, vitae dignissim urna. Praesent interdum molestie enim. Curabitur maximus efficitur neque, eu egestas tellus molestie porta. Duis vitae nunc vehicula turpis rhoncus vehicula cursus condimentum ante. Aliquam molestie turpis quis nunc tristique hendrerit. Ut sed lectus id sem condimentum porta nec non diam. Nunc accumsan, nulla ut lobortis molestie, odio mi molestie ligula, nec mollis sapien sem a quam. Donec porta, ex at pellentesque consectetur, nisl augue tempor nibh, sed fermentum lacus risus a libero. Sed mauris eros, auctor in varius non, porta vel nisl. Aliquam elementum nibh vitae est cursus dapibus. Mauris dictum quam quam, id tristique magna efficitur ac. Pellentesque sed augue sit amet lacus ornare sollicitudin. Aliquam sodales feugiat dictum. Sed faucibus euismod leo, nec porttitor neque dictum a. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi convallis nibh id ex consequat lobortis.

            Pellentesque tempus rutrum massa non ornare. Cras facilisis tempus felis, sit amet euismod velit fringilla eget. Nullam justo mi, dapibus placerat ipsum quis, luctus eleifend mauris. Morbi enim ante, tempor ac nulla et, posuere mattis dui. Ut sed nulla ipsum. Vivamus at ultricies odio. Aenean vel quam sem. Quisque a lorem rutrum ex auctor tincidunt. Donec ut pulvinar quam. Nam non vestibulum magna. Ut ut augue porta, maximus ipsum eget, aliquet arcu. Praesent tincidunt congue egestas.

            Sed consequat scelerisque nisi et efficitur. Nunc lectus nibh, blandit a semper et, lobortis eu turpis. Nullam ut posuere sapien. Nullam ornare molestie felis, quis aliquet tortor viverra egestas. Vivamus et ornare neque, id tempor ipsum. Quisque placerat accumsan sapien vel mollis. Vivamus posuere ante et erat mattis, nec malesuada nunc sodales. Etiam pharetra molestie massa, vitae varius arcu bibendum vitae. Suspendisse rhoncus orci eu eleifend ultricies.

            Suspendisse pharetra tincidunt tortor nec efficitur. Proin gravida venenatis lectus, et eleifend est ornare quis. Fusce mi lacus, lacinia quis dolor eget, imperdiet pharetra mauris. Nullam non placerat risus. Donec et egestas mauris. Etiam interdum nec libero vitae ultricies. Nullam consectetur lorem turpis, non sagittis mi blandit non. Praesent ullamcorper sapien neque, gravida consectetur odio malesuada vitae. Phasellus ex dolor, vestibulum eget ligula a, pulvinar posuere urna. Ut diam lectus, commodo nec pulvinar sit amet, egestas eu augue. Interdum et malesuada fames ac ante ipsum primis in faucibus. Proin tortor justo, pulvinar eget fermentum ac, ullamcorper sit amet mauris. In lacinia suscipit lacinia. Donec eleifend vulputate ullamcorper. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Etiam lectus elit, mattis quis diam at, sollicitudin facilisis erat.

            Nullam turpis lacus, rutrum vitae nibh eget, ornare scelerisque velit. Etiam maximus urna non arcu elementum pharetra. Suspendisse libero diam, blandit eu risus nec, volutpat dapibus justo. Pellentesque quis pharetra nunc. In et consequat dui. Maecenas condimentum aliquam augue, vitae aliquet sem porta nec. Maecenas ullamcorper odio a sem feugiat maximus. Etiam ornare ante urna, sed cursus magna consectetur ut. Sed lorem libero, pretium nec maximus et, convallis eget massa. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Nunc maximus at tortor eu sodales.
            """.split("\\s+");

    private static final int VARCHAR_MAX = 255;

    private WideRandomValues() {
    }

    static Wide randomWide() {
        Wide wide = new Wide();
        fillRandomLetterFields(wide);
        return wide;
    }

    static void fillRandomLetterFields(Wide wide) {
        wide.setA(randomInt());
        wide.setB(randomDouble());
        wide.setC(randomVarchar());
        wide.setD(randomInt());
        wide.setE(randomDouble());
        wide.setF(randomVarchar());
        wide.setG(randomInt());
        wide.setH(randomDouble());
        wide.setI(randomVarchar());
        wide.setJ(randomInt());
        wide.setK(randomDouble());
        wide.setL(randomVarchar());
        wide.setM(randomInt());
        wide.setN(randomDouble());
        wide.setO(randomVarchar());
        wide.setP(randomInt());
        wide.setQ(randomDouble());
        wide.setR(randomVarchar());
        wide.setS(randomInt());
        wide.setT(randomDouble());
        wide.setU(randomVarchar());
        wide.setV(randomInt());
        wide.setW(randomDouble());
        wide.setX(randomVarchar());
        wide.setY(randomInt());
        wide.setZ(randomDouble());
    }

    private static int randomInt() {
        return ThreadLocalRandom.current().nextInt(1, 10_000);
    }

    private static double randomDouble() {
        double raw = ThreadLocalRandom.current().nextDouble(1.0, 10.0);
        return Math.round(raw * 100.0) / 100.0;
    }

    private static String randomVarchar() {
        final int maxLength = 4096;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int length = random.nextInt(LOREM_IPSUM.length / 2, LOREM_IPSUM.length);
        int maxStart = LOREM_IPSUM.length - length;
        int start = maxStart > 0 ? random.nextInt(maxStart + 1) : 0;
        String value = String.join(" ", Arrays.copyOfRange(LOREM_IPSUM, start, start + length));
        return value.substring(0, Math.min(value.length(), maxLength));
    }
}
