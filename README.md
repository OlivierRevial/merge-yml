merge-yml
=========

Merge multiple YML files into a single file, optionnaly writing the result in a destination file

This code is largely inspired from Jonathan Cobb "merge-yml" module here.

(c) Copyright 2013 Olivier Revial.
This code is available under the Apache License, version 2: http://www.apache.org/licenses/LICENSE-2.0.html

## Build

    mvn -P uberjar package
    
Requires 'maven' build automation tool.

Install maven on OS X using [homebrew](http://brew.sh/): `brew install maven`

## Usage

    ./bin/merge-yml.sh file1.yml file2.yml ... > merged-result.yml

Files are merged in order, such that files listed later will override files listed earlier.

The merged result is written to stdout. Logs (for info & errors) are written to stderr.
