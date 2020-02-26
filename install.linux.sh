mypath="$(pwd)"
sudo  mkdir -p "$1"
cd   "$1" || return
git   clone https://SimonCanJer:PolarWolf1957@github.com/SimonCanJer/microhazle.git
cd    microhazle || exit
mvn install
cd    "$mypath" || return
mvn install
cd     micro-hazel-spring-examples\target || return
        java -jar micro-hazel-spring-examples-1.0.4-exec.jar  backend &
sleep 40
exec   java -jar micro-hazel-spring-examples-1.0.4-exec.jar  facade  &
cd     "$mypath"

