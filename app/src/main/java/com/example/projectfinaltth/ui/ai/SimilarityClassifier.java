package com.example.projectfinaltth.ui.ai;


public interface SimilarityClassifier {
    // Lớp Recognition chứa thông tin về kết quả nhận dạng

    class Recognition {

        private final String id; // ID của kết quả nhận dạng
        private final String title; // Tiêu đề của kết quả nhận dạng
        private final Float distance; // Khoảng cách giữa các nhúng khuôn mặt
        private Object extra; // Dữ liệu bổ sung (nhúng của khuôn mặt)

        // Constructor để khởi tạo các giá trị của Recognition

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
