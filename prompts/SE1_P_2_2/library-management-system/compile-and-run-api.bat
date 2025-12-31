@echo off
echo 📚 کامپایل پروژه کتابخانه برای API...

:: ایجاد پوشه classes اگر وجود ندارد
if not exist "target\classes" mkdir target\classes

:: کامپایل همه فایل‌های جاوا
javac -d target\classes -cp ".;target\classes" ^
    src\main\java\ir\university\library\model\*.java ^
    src\main\java\ir\university\library\service\*.java ^
    src\main\java\ir\university\library\statistics\*.java ^
    src\main\java\ir\university\library\api\*.java ^
    src\main\java\ir\university\library\ui\*.java

if errorlevel 1 (
    echo ❌ خطا در کامپایل
    pause
    exit /b 1
)

echo ✅ کامپایل موفقیت‌آمیز بود!

:: اجرای API سرور
echo 🚀 در حال راه‌اندازی API سرور...
java -cp "target\classes" ir.university.library.api.LibraryApiStarter
pause