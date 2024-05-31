package com.example.projectfinaltth.ui.ai;


public interface SimilarityClassifier {
    class Recognition {

        private final String id;
        private final String title;


        private final Float distance;
        private Object extra;

        public Recognition(
                final String id, final String title, final Float distance) {
            this.id = id;
            this.title = title;
            this.distance = distance;
            this.extra = null;

        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getDistance() {
            return distance;
        }

        public void setExtra(Object extra) {
            this.extra = extra;
        }
        public Object getExtra() {
            return this.extra;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (distance != null) {
                resultString += String.format("(%.1f%%) ", distance * 100.0f);
            }

            return resultString.trim();
        }

    }
}
