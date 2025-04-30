package com.example.appdelishorder.Utils;

public class OrderStatusUtil {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_SHIPPING = 2;
    public static final int STATUS_DELIVERED = 3;
    public static final int STATUS_CANCELLED = 4;

    // Get display name for status
    public static String getStatusName(int status) {
        switch (status) {
            case STATUS_PENDING:
                return "Chờ xác nhận";
            case STATUS_CONFIRMED:
                return "Đang chuẩn bị";
            case STATUS_SHIPPING:
                return "Đang giao hàng";
            case STATUS_DELIVERED:
                return "Đã giao";
            case STATUS_CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    // Get color for status (for UI display)
    public static int getStatusColor(int status) {
        switch (status) {
            case STATUS_PENDING:
                return 0xFFFFA000; // Amber
            case STATUS_CONFIRMED:
                return 0xFF2196F3; // Blue
            case STATUS_SHIPPING:
                return 0xFF9C27B0; // Purple
            case STATUS_DELIVERED:
                return 0xFF4CAF50; // Green
            case STATUS_CANCELLED:
                return 0xFFF44336; // Red
            default:
                return 0xFF757575; // Grey
        }
    }

    // Check if status is valid
    public static boolean isValidStatus(int status) {
        return status >= STATUS_PENDING && status <= STATUS_CANCELLED;
    }

    // Check if order can be cancelled
    public static boolean canCancel(int status) {
        return status == STATUS_PENDING || status == STATUS_CONFIRMED;
    }

    // Get next status in typical workflow
    public static int getNextStatus(int currentStatus) {
        if (currentStatus >= STATUS_PENDING && currentStatus < STATUS_DELIVERED) {
            return currentStatus + 1;
        }
        return currentStatus; // No change if already delivered or cancelled
    }
    // Get status code from status name
    public static int getStatusCode(String statusName) {
        if (statusName == null) return -1;

        // Chuyển đổi tên trạng thái tiếng Việt thành mã trạng thái
        switch (statusName.toLowerCase()) {
            case "chờ xác nhận":
            case "pending":
            case "0":
                return STATUS_PENDING;

            case "đang chuẩn bị":
            case "confirmed":
            case "1":
                return STATUS_CONFIRMED;

            case "đang giao hàng":
            case "shipping":
            case "2":
                return STATUS_SHIPPING;

            case "đã giao":
            case "delivered":
            case "3":
                return STATUS_DELIVERED;

            case "đã hủy":
            case "cancelled":
            case "4":
                return STATUS_CANCELLED;

            default:
                // Thử chuyển đổi trực tiếp nếu là chuỗi số
                try {
                    int status = Integer.parseInt(statusName);
                    if (isValidStatus(status)) {
                        return status;
                    }
                } catch (NumberFormatException e) {
                    // Không phải là số, bỏ qua
                }
                return -1; // Không xác định
        }
    }

}
