package se.kth.oberg.matn.merrills;

/**
 * Created by Rugvip on 2013-11-28.
 */
public class Dimentionalizer {
    public static Dimentionalization dimentionalize(int width, int height) {
        return dimentionalize(width, height, new Dimentionalization());
    }
    public static Dimentionalization dimentionalize(int width, int height, Dimentionalization dimentionalization) {
        float size = width < height ? width * 9.0f / 10.0f : height * 9.0f / 10.0f;
        dimentionalization.setSize((int) size);

        float offsetX = (width - size) / 2.0f;
        dimentionalization.setOffsetX((int) offsetX);

        if (width >= height) {
            float offsetY = (height - size) / 2.0f;
            dimentionalization.setOffsetY((int) offsetY);
        } else {
            dimentionalization.setOffsetY((int) offsetX);
        }

        return dimentionalization;
    }

    public static class Dimentionalization {
        private int size;
        private int offsetX;
        private int offsetY;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }
    }
}
