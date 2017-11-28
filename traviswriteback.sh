#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_website_files() {
  git checkout -b tool
  git add . *.html
  git commit --message "Added reports from Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add origin-pages https://${PUSH_BACK}@github.com:lucasbuschlinger/BachelorPraktikum.git > /dev/null 2>&1
  git push --quiet -u origin/feature tool
}

setup_git
commit_website_files
upload_files