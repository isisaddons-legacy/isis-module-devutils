# isis-module-devutils #

[![Build Status](https://travis-ci.org/isisaddons/isis-module-devutils.png?branch=master)](https://travis-ci.org/isisaddons/isis-module-devutils)

This module, intended for use with [Apache Isis](http://isis.apache.org), provides a number of development-time 
utilities, mostly related to accessing/interacting with the Isis metamodel. These are visible in the UI either as menu 
actions or as contributed actions. 

## Screenshots ##

The following screenshots show an example app's usage of the module with some sample fixture data:

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


## How to run the Demo App ##

The prerequisite software is:

* Java JDK 8 (>= 1.9.0) or Java JDK 7 (<= 1.8.0)
* [maven 3](http://maven.apache.org) (3.2.x is recommended).

To build the demo app:

    git clone https://github.com/isisaddons/isis-module-devutils.git
    mvn clean install

To run the demo app:

    mvn antrun:run -P self-host
    
Then log on using user: `sven`, password: `pass`


## Relationship to Apache Isis Core ##

Isis Core 1.6.0 included the `org.apache.isis.module:isis-module-devutils:1.6.0` Maven artifact.  This module is a
direct copy of that code, with the following changes:

* package names have been altered from `org.apache.isis` to `org.isisaddons.module.devutils`
* for simplicity, the applib and impl submodules have been combined into a single module
* the `DeveloperUtilitiesServiceDefault` is now annotated with `@DomainService` so does not need explicitly registering
  in `isis.properties`.

Otherwise the functionality is identical; warts and all!

Isis 1.7.0 no longer ships with `org.apache.isis.module:isis-module-devutils`;
use this addon module instead.


## How to configure/use ##

You can either use this module "out-of-the-box", or you can fork this repo and extend to your own requirements. 

#### "Out-of-the-box" ####

To use "out-of-the-box":

* update your classpath by adding this dependency in your dom project's `pom.xml`:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.isisaddons.module.devutils&lt;/groupId&gt;
        &lt;artifactId&gt;isis-module-devutils-dom&lt;/artifactId&gt;
        &lt;version&gt;1.8.0&lt;/version&gt;
    &lt;/dependency&gt;
</pre>


#### "Out-of-the-box" (-SNAPSHOT) ####

If you want to use the current `-SNAPSHOT`, then the steps are the same as above, except:

* when updating the classpath, specify the appropriate -SNAPSHOT version:

<pre>
    &lt;version&gt;1.9.0-SNAPSHOT&lt;/version&gt;
</pre>

* add the repository definition to pick up the most recent snapshot (we use the Cloudbees continuous integration service).  We suggest defining the repository in a `<profile>`:

<pre>
    &lt;profile&gt;
        &lt;id&gt;cloudbees-snapshots&lt;/id&gt;
        &lt;activation&gt;
            &lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
        &lt;/activation&gt;
        &lt;repositories&gt;
            &lt;repository&gt;
                &lt;id&gt;snapshots-repo&lt;/id&gt;
                &lt;url&gt;http://repository-estatio.forge.cloudbees.com/snapshot/&lt;/url&gt;
                &lt;releases&gt;
                    &lt;enabled&gt;false&lt;/enabled&gt;
                &lt;/releases&gt;
                &lt;snapshots&gt;
                    &lt;enabled&gt;true&lt;/enabled&gt;
                &lt;/snapshots&gt;
            &lt;/repository&gt;
        &lt;/repositories&gt;
    &lt;/profile&gt;
</pre>

#### Forking the repo ####

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


## Change Log ##

* `1.8.0` - released against Isis 1.8.0, support new @XxxLayout annotations; domain events for actions.
* `1.7.0` - released against Isis 1.7.0
* `1.6.1` - closes issue#1.
* `1.6.0` - re-released as part of isisaddons, with classes under package `org.isisaddons.module.devutils`


## Legal Stuff ##
 
#### License ####

    Copyright 2014-2015 Dan Haywood

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

#### Release to Maven Central ####

The `release.sh` script automates the release process.  It performs the following:

* performs a sanity check (`mvn clean install -o`) that everything builds ok
* bumps the `pom.xml` to a specified release version, and tag
* performs a double check (`mvn clean install -o`) that everything still builds ok
* releases the code using `mvn clean deploy`
* bumps the `pom.xml` to a specified release version

For example:

    sh release.sh 1.9.0 \
                  1.10.0-SNAPSHOT \
                  dan@haywood-associates.co.uk \
                  "this is not really my passphrase"
    
where
* `$1` is the release version
* `$2` is the snapshot version
* `$3` is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* `$4` is the corresponding passphrase for that secret key.

Other ways of specifying the key and passphrase are available, see the `pgp-maven-plugin`'s 
[documentation](http://kohsuke.org/pgp-maven-plugin/secretkey.html)).

If the script completes successfully, then push changes:

    git push origin master
    git push origin 1.9.0

If the script fails to complete, then identify the cause, perform a `git reset --hard` to start over and fix the issue
before trying again.  Note that in the `dom`'s `pom.xml` the `nexus-staging-maven-plugin` has the 
`autoReleaseAfterClose` setting set to `true` (to automatically stage, close and the release the repo).  You may want
to set this to `false` if debugging an issue.
 
According to Sonatype's guide, it takes about 10 minutes to sync, but up to 2 hours to update [search](http://search.maven.org).
