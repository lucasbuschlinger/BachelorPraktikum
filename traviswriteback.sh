#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_website_files() {
  git checkout -b feature/tool
  git add . *.html
  git commit --message "Added reports from Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add BP https://${PUSH_BACK}@github.com:lucasbuschlinger/BachelorPraktikum.git
  git push --quiet --set-upstream BP feature/tool
}

setup_git
commit_website_files
upload_files