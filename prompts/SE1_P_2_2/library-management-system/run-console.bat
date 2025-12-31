@echo off
echo 📚 سیستم مدیریت کتابخانه دانشگاه
echo =================================

:: بررسی وجود فایل‌های کامپایل شده
if not exist "target\classes" (
    echo ⚠️ فایل‌های کامپایل شده یافت نشد!
    echo در حال کامپایل پروژه...
    call compile-and-run.bat
    exit /b
)

echo 🚀 در حال اجرای رابط کاربری کنسول...
java -cp "target\classes" ir.university.library.ui.LibraryManagementSystem
pause