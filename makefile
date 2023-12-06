compile:
    git pull
    rm -rf out
    javac -d out src/*.java src/data/*.java