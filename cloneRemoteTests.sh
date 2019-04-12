# GITHUB_REP and SUBDIR environment variables are needed
# e.g export GITHUB_REPO=https://github.com/panossot/RemoteTestRepo.git
#     export SUBDIR=RemoteTestRepo/src/java/main/org

if [ -z "$GITHUB_REPO" ]; then
    echo "GITHUB_REPO is not set."
    exit 1
fi  

if [ -z "$SUBDIR" ]; then
    echo "SUBDIR is not set."
    exit 1
fi

rm -R remoteSrc
mkdir remoteSrc
cd remoteSrc
# echo "========= " . $GITHUB_REPO . " " . $SUBDIR
git clone $GITHUB_REPO
cd ..
cp -R ./remoteSrc/$SUBDIR ./modules/src/main/java



