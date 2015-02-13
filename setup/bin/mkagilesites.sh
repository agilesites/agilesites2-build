#!/bin/bash
export JAVA_HOME="${1?:java home}"
cd "$(dirname $0)"
cd ..
export BASE=$(pwd)
cat <<EOF >"${2:?output file}"
#!/bin/bash
export PATH="$JAVA_HOME/bin:$BASE/bin:$BASE/home/bin:$PATH"
"$JAVA_HOME/bin/java" -Xms128m -Xmx512m -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -Dagilesites.base=$BASE -jar $BASE/bin/sbt-launch.jar @$BASE/bin/agilesites.boot.properties \$*
EOF
