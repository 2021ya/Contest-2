@echo off
echo AI旅游规划系统 - 后台管理系统
echo ================================
echo.
echo 正在安装依赖...
call npm install
echo.
echo 依赖安装完成！
echo.
echo 启动开发服务器...
echo 系统将在 http://localhost:3001 启动
echo.
echo 默认登录信息：
echo 用户名：admin
echo 密码：123456
echo.
call npm run dev
pause

