gitPush() {

  br=`git symbolic-ref --short -q HEAD`


if [ "$br" = "developer" ] ; then
    git add ./
    git commit -m'crontab task'
    git push origin developer

else
    echo "current branch is not developer"
fi
}
gitPush

