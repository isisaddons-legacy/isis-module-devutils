# isis-module-devutils #

[![Build Status](https://travis-ci.org/isisaddons/isis-module-devutils.png?branch=master)](https://travis-ci.org/isisaddons/isis-module-devutils)

This module, intended for use with [Apache Isis](http://isis.apache.org), provides a number of development-time 
utilities, mostly related to accessing/interacting with the Isis metamodel. These are visible in the UI either as menu 
actions or as contributed actions. 

All actions are annotated with `@Prototype`, so are suppressed in production mode.

## Screenshots ##

The following screenshots show an example app's usage of the module.

#### Installing the Fixture Data ####

Install sample fixtures: 

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/010-install-fixtures.png)

#### Downloading a single .layout.json file ####

The module contributes the `download layouts` action:

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/020-download-layout.png)

... which downloads a JSON file:

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/030-layout-downloaded.png)

... that contains a `.layout.json` corresponding to the current Isis metamodel (as specified by any annotations etc).

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/040-layout.png)

The developer can copy the JSON file into the correct location (alongside the `.java` class file) and update as required.
The JSON files takes precedence over any annotations.

#### Downloading all .layout.json file ####

If there are many (or all) entities that require a `.layout.json` file, then a zip of all the layouts can be downloaded:

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/050-download-layouts.png)

#### Downloading the metamodel as a CSV  ####

To support code reviews and adhoc analysis of the code base, the metamodel can be downloaded as a CSV file:

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/070-download-metamodel.png)

... which can then be opened in a spreadsheet or other tool:

![](https://raw.github.com/isisaddons/isis-module-devutils/master/images/090-metamodel-in-excel.png)


## Relationship to Apache Isis Core ##

Isis Core 1.6.0 included the `org.apache.isis.core:isis-module-devutils:1.6.0` Maven artifact.  This module is a
direct copy of that code, with the following changes:

* package names have been altered from `org.apache.isis` to `org.isisaddons.module.devutils`
* for simplicity, the applib and impl submodules have been combined into a single
* the `DeveloperUtilitiesServiceDefault` is now annotated with `@DomainService` so does not need explicitly registering
  in `isis.properties`.

Otherwise the functionality is identical; warts and all!

At the time of writing the plan is to remove this module from Isis Core (so it won't be in Isis 1.7.0), and instead 
continue to develop it solely as one of the [Isis Addons](http://www.isisaddons.org) modules.


## How to configure/use ##

You can either use this module "out-of-the-box", or you can fork this repo and extend to your own requirements. 

To use "out-of-the-box":

* update your classpath by adding this dependency in your dom project's `pom.xml`:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.isisaddons.module.devutils&lt;/groupId&gt;
        &lt;artifactId&gt;isis-module-devutils-dom&lt;/artifactId&gt;
        &lt;version&gt;1.6.0&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

If instead you want to extend this module's functionality, then we recommend that you fork this repo.  The repo is 
structured as follows:

* `pom.xml   ` - parent pom
* `dom       ` - the module implementation, depends on Isis applib
* `fixture   ` - fixtures, holding a sample domain objects and fixture scripts; depends on `dom`
* `integtests` - integration tests for the module; depends on `fixture`
* `webapp    ` - demo webapp (see above screenshots); depends on `dom` and `fixture`

Only the `dom` project is released to     Check for versions available in the 
[Maven Central Repo](http://search.maven.org/#search|ga|1|isis-module-audit-dom).  The versions of the other modules 
are purposely left at `0.0.1-SNAPSHOT` because they are not intended to be released.


## Related Modules/Services ##

The [Isis Addons Security Module](http://github.com/isisaddons/isis-module-security) also exposes aspects of the
Isis metamodel (in its `ApplicationFeatures` service and `ApplicationFeature` class).  In the future this functionality
within the security module may move into this devutils module. 


## Legal Stuff ##
 
#### License ####

    Copyright 2014 Dan Haywood

    Licensed under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.


#### Dependencies ####

There are no third-party dependencies.

##  Maven deploy notes ##

Only the `dom` module is deployed, and is done so using Sonatype's OSS support (see 
[user guide](http://central.sonatype.org/pages/apache-maven.html)).

#### Release to Sonatype's Snapshot Repo ####

To deploy a snapshot, use:

    pushd dom
    mvn clean deploy
    popd

The artifacts should be available in Sonatype's 
[Snapshot Repo](https://oss.sonatype.org/content/repositories/snapshots).

#### Release to Maven Central (scripted process) ####

The `release.sh` script automates the release process.  It performs the following:

* perform sanity check (`mvn clean install -o`) that everything builds ok
* bump the `pom.xml` to a specified release version, and tag
* perform a double check (`mvn clean install -o`) that everything still builds ok
* release the code using `mvn clean deploy`
* bump the `pom.xml` to a specified release version

For example:

    sh release.sh 1.6.0 \
                  1.6.1-SNAPSHOT \
                  dan@haywood-associates.co.uk \
                  "this is not really my passphrase"
    
where
* `$1` is the release version
* `$2` is the snapshot version
* `$3` is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* `$4` is the corresponding passphrase for that secret key.

If the script completes successfully, then push changes:

    git push
    
If the script fails to complete, then identify the cause, perform a `git reset --hard` to start over and fix the issue
before trying again.

#### Release to Maven Central (manual process) ####

If you don't want to use `release.sh`, then the steps can be performed manually.

To start, call `bumpver.sh` to bump up to the release version, eg:

     `sh bumpver.sh 1.6.0`

which:
* edit the parent `pom.xml`, to change `${isis-module-command.version}` to version
* edit the `dom` module's pom.xml version
* commit the changes
* if a SNAPSHOT, then tag

Next, do a quick sanity check:

    mvn clean install -o
    
All being well, then release from the `dom` module:

    pushd dom
    mvn clean deploy -P release \
        -Dpgp.secretkey=keyring:id=dan@haywood-associates.co.uk \
        -Dpgp.passphrase="literal:this is not really my passphrase"
    popd

where (for example):
* "dan@haywood-associates.co.uk" is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* the pass phrase is as specified as a literal

Other ways of specifying the key and passphrase are available, see the `pgp-maven-plugin`'s 
[documentation](http://kohsuke.org/pgp-maven-plugin/secretkey.html)).

If (in the `dom`'s `pom.xml`) the `nexus-staging-maven-plugin` has the `autoReleaseAfterClose` setting set to `true`,
then the above command will automatically stage, close and the release the repo.  Sync'ing to Maven Central should 
happen automatically.  According to Sonatype's guide, it takes about 10 minutes to sync, but up to 2 hours to update 
[search](http://search.maven.org).

If instead the `autoReleaseAfterClose` setting is set to `false`, then the repo will require manually closing and 
releasing either by logging onto the [Sonatype's OSS staging repo](https://oss.sonatype.org) or alternatively by 
releasing from the command line using `mvn nexus-staging:release`.

Finally, don't forget to update the release to next snapshot, eg:

    sh bumpver.sh 1.6.1-SNAPSHOT

and then push changes.
