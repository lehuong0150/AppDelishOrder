package com.example.appdelishorder.Contract;



public interface vnPayContract {
    interface View {
        void onVNPayUrlReceived(String url);
        void onVNPayError(String message);
    }
    interface Presenter {
        void createVNPayPayment(String amount, String orderId);
    }
}
