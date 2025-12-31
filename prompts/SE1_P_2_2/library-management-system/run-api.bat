@echo off
echo 🌐 API سیستم مدیریت کتابخانه دانشگاه
echo ======================================

:: بررسی وجود فایل‌های کامپایل شده
if not exist "target\classes" (
    echo ⚠️ فایل‌های کامپایل شده یافت نشد!
    echo در حال کامپایل پروژه...
    call compile-and-run-api.bat
    exit /b
)

echo 🚀 در حال راه‌اندازی API سرور...
java -cp "target\classes" ir.university.library.api.LibraryApiStarter
pause