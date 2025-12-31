@echo off
echo 📚 کامپایل پروژه کتابخانه...

:: ایجاد پوشه classes اگر وجود ندارد
if not exist "target\classes" mkdir target\classes

:: کامپایل همه فایل‌های جاوا
javac -d target\classes -cp ".;target\classes" ^
    src\main\java\ir\university\library\model\*.java ^
    src\main\java\ir\university\library\service\*.java ^
    src\main\java\ir\university\library\statistics\*.java ^
    src\main\java\ir\university\library\ui\*.java ^
    src\main\java\ir\university\library\api\*.java ^
    src\main\java\ir\university\library\*.java

if errorlevel 1 (
    echo ❌ خطا در کامپایل
    pause
    exit /b 1
)

echo ✅ کامپایل موفقیت‌آمیز بود!

:: اجرای برنامه
echo 🚀 در حال اجرای سیستم کتابخانه...
java -cp "target\classes" ir.university.library.ui.LibraryManagementSystem
pause