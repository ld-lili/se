package ir.university.library.api;

import ir.university.library.service.LibraryManager;
import java.io.IOException;

public class LibraryApiStarter {
    public static void main(String[] args) {
        try {
            System.out.println("📚 سیستم مدیریت کتابخانه دانشگاه - نسخه API");
            System.out.println("===========================================");

            LibraryManager libraryManager = new LibraryManager();
            ApiServer apiServer = new ApiServer(libraryManager);

            apiServer.start();

            System.out.println("\n✅ API Server آماده است!");
            System.out.println("🌐 Endpointها:");
            System.out.println("   1. POST   /api/auth/register    - ثبت‌نام دانشجو");
            System.out.println("   2. POST   /api/auth/login       - ورود به سیستم");
            System.out.println("   3. GET    /api/books            - لیست کتاب‌ها");
            System.out.println("   4. GET    /api/books/{id}       - جزئیات کتاب");
            System.out.println("   5. POST   /api/books            - ایجاد کتاب جدید (کارمند)");
            System.out.println("   6. PUT    /api/books/{id}       - ویرایش کتاب (کارمند)");
            System.out.println("   7. GET    /api/books/search     - جستجوی کتاب");
            System.out.println("   8. POST   /api/borrow/request   - درخواست امانت (دانشجو)");
            System.out.println("   9. GET    /api/borrow/requests/pending - درخواست‌های در انتظار (کارمند)");
            System.out.println("   10. POST  /api/borrow/requests/{id}/approve - تایید درخواست (کارمند)");
            System.out.println("   11. POST  /api/borrow/requests/{id}/reject - رد درخواست (کارمند)");
            System.out.println("   12. POST  /api/borrow/{id}/return - بازگشت کتاب (کارمند)");
            System.out.println("   13. GET   /api/students/{id}    - پروفایل دانشجو");
            System.out.println("   14. PUT   /api/students/{id}/status - تغییر وضعیت دانشجو (کارمند)");
            System.out.println("   15. GET   /api/students/{id}/borrow-history - تاریخچه امانت دانشجو (کارمند)");
            System.out.println("   16. GET   /api/stats/summary    - آمار کلی");
            System.out.println("   17. GET   /api/stats/borrows    - آمار امانت‌ها (مدیر)");
            System.out.println("   18. GET   /api/stats/employees/{id}/performance - عملکرد کارمند (مدیر)");
            System.out.println("   19. GET   /api/stats/top-delayed - دانشجویان با بیشترین تاخیر (مدیر)");
            System.out.println("   20. POST  /api/admin/employees  - ایجاد کارمند جدید (مدیر)");
            System.out.println("   21. GET   /api/admin/employees  - لیست کارمندان (مدیر)");

            System.out.println("\n👥 اطلاعات پیش‌فرض برای تست:");
            System.out.println("   👑 مدیر: manager / manager123");
            System.out.println("   👨‍💼 کارمندان: emp1, emp2, emp3 / 1234");
            System.out.println("   👨‍🎓 دانشجویان: stu1, stu2, stu3 / 1234");

            System.out.println("\n📋 برای توقف سرور: Ctrl+C");
            System.out.println("💡 از Postman یا curl برای تست API استفاده کنید");

            // نگه داشتن برنامه در حال اجرا
            System.in.read();
            apiServer.stop();

        } catch (IOException e) {
            System.err.println("❌ خطا در راه‌اندازی سرور: " + e.getMessage());
            e.printStackTrace();
        }
    }
}