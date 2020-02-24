set mypath=%cd%
echo %mypath%
mkdir %1
cd %1
git clone https://github.com/SimonCanJer/microhazle.git
cd microhazle
call mvn install
cd %mypath%
call mvn install
cd micro-hazel-spring-examples\target
start  java -jar  micro-hazel-spring-examples-1.0.4-exec.jar  backend
timeout 45
start  java -jar  micro-hazel-spring-examples-1.0.4-exec.jar  facade
cd %mypath%

