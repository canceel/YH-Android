gitPush() {
git symbolic-ref --short -q HEAD

  br=`git branch | grep "*"`


if [ "${br/* /}" = "developer" ] ; then
    git add ./
    git commit -m'crontab task'
    git push origin developer

else
    echo "no"
fi
}
gitPush

