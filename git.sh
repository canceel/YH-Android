gitPush() {

  br=`git symbolic-ref --short -q HEAD`
  timestamp=$(date "+%Y-%m-%d %H:%M:%S")

if [ "$br" = "developer" ] ; then
    git add ./
    git commit -m'crontab task ${timestamp}'
    git push origin developer

else
    echo "current branch is not developer"
fi
}
gitPush

