# MetricMiner 2

## Are you looking for a help with your empirical study?

MetricMiner is a Java framework that helps developers on mining software repositories. With it, you can easily extract information from any Git repository, such as commits, developers, modifications, diffs, and source codes, and quickly export CSV files.

# Documentation

## Getting Started

You simply have to start a Java Project in Eclipse. MetricMiner is on Maven, so you can download all its dependencies by only adding this to your pom.xml. Or, if you want, you can download a [skeleton project](download/metricminer2-skeleton.zip):

```
<dependency>
	<groupid>br.usp</groupid>
	<artifactid>metricminer</artifactid>
	<version>2.1.0</version>
</dependency> 
```

Always use the latest version in Maven. You can see them here: [http://www.mvnrepository.com/artifact/br.usp/metricminer](http://www.mvnrepository.com/artifact/br.usp/metricminer) . You can also see a [fully function pom.xml example](https://gist.github.com/mauricioaniche/3eba747930aea97e4adb).

MetricMiner needs a _Study_. The interface is quite simple: a single _execute()_ method:

All the magic goes inside this method. In there, you will have to configure your study, projects to analyze, metrics to be executed, and output files. Take a look in the example. That's what we have to configure:

Let's start with something simple: we will print the name of the developers for each commit. For now, you should not care about all possible configurations. We will analyze all commits in the project at "/Users/mauricioaniche/workspace/metricminer2", outputing _DevelopersVisitor_ to "/Users/mauricioaniche/Desktop/devs.csv".

*   in(): We use to configure the project (or projects) that will be analyzed.
*   through(): The list of commits to analyze. We want all of them.
*   fromTheBeginning(): Commits will be analysed from the first commit in history to the most recent. Default is the opposite.
*   process(): Visitors that will pass in each commit.
*   mine(): The magic starts!

In practice, MetricMiner will open the Git repository and will extract all information that is inside. Then, the framework will pass each commit to all processors. Let's write our first _DevelopersProcessor_. It is fairly simple. All we will do is to implement _CommitVisitor_. And, inside of _process()_, we print the commit hash and the name of the developer. MetricMiner gives us nice objects to play with all the data:

That's it, we are ready to go! If we execute it, we will have the CSV printed into "/Users/mauricioaniche/Desktop/devs.csv". [Take a look](download/devs.csv).

**I bet you never found a framework simple as this one!**

## Configuring the project

The first thing you configure in MetricMiner is the project you want to analyze. MetricMiner currently only suports Git repositories. The _GitRepository_ class contains two factory methods to that:

*   _singleProject(path)_: When you want to analyze a single repository.
*   _allProjectsIn(path)_: When you want to analyze many repositories. In this case, you should pass a path to which all projects are sub-directories of it. Each directory will be considered as a project to MetricMiner.

## Logging

MetricMiner uses log4j to print useful information about its execution. We recommend tou to have a log4.xml:

## Selecting the Commit Range

MetricMiner allows you to select the range of commits to be processed. The class _Commits_ contains different methods to that:

*   _all()_: All commits. From the first to the last.
*   _onlyInHead()_: It only analyzes the most recent commit.
*   _single(hash)_: It only analyzes a single commit with the provided hash.
*   _monthly(months)_: It selects one commit per month, from the beginning to the end of the repo.
*   _list(commits...)_: The list of commits to be processed.
*   _range(start,end)_: The range of commits, starting at "start" hash, ending at "end" hash.

One interesting thing about MetricMiner is that is avoids huge commits. When a commit contains too many files (> 50), it will be ignored.

## Getting Modifications

You can get the list of modified files, as well as their diffs and current source code. To that, all you have to do is to get the list of _Modification_s that exists inside _Commit_. A _Commit_ contains a hash, a committer (name and email), an author (name, and email) a message, the date, its parent hash, and the list of modification.

A _Modification_ contains a type (ADD, COPY, RENAME, DELETE, MODIFY), a diff (with the exact format Git delivers) and the current source code. Remember that it is up to you to handle deleted or renamed files in your study.

## Branches

MetricMiner puts all commits from all branches in a single sentence. It means that different commits from different branches will appear. It is your responsibility to filter the branches in your _CommitVisitor_.

The _Commit_ class contains the _getBranches()_ method. It returns the list of branches in which a commit belongs to. If you want to use only commits in the master branch, you can simply check whether 'master' in inside this set.

Note about the implementation: This is not supported by JGit, so it makes use of Git directly.

## Managing State in the Visitor

If you need to, you can store state in your visitors. As an example, if you do not want to process a huge CSV, you can pre-process something before. As an example, if you want to count the total number of modified files per developer, you can either output all developers and the quantity of modifications, and then sum it later using your favorite database, or do the math in the visitor. If you decide to do it, it will be your responsibility to save the results afterwards.

## Parsing Code

You have entire source code of the repository. You may want to analyze it. MetricMiner comes with JDT and ANTLR bundled. JDT is the Eclipse internal parser, so you will not regret to use it.

Let's say we decide to count the quantity of methods in each modified file. All we have to do is to create a _CommitVisitor_, the way we are used to. This visitor will use our _JDTRunner_ to invoke a JDT visitor (yes, JDT uses visitors as well). Notice that we executed the JDT visitor, and then wrote the result.

The visitor is quite simple. It has methods for all different nodes in the file. All you have to do is to visit the right node. As an example, we will visit _MethodDeclaration_ and count the number of times it is invoked (one per each method in this file). If you want to see all methods available, check the documentation for [ASTVisitor](http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fdom%2FASTVisitor.html).

## Getting the Current Revision

If you need more than just the metadata from the commit, you may also check out to the revision to access all files. This may be useful when you need to parse all files inside that revision.

To that, we will _checkout()_ the revision, and get all _files()_. It returns the list of all files in the project at that moment. Then, it is up to you to do whatever you want. In here, we will use our _NumberOfMethodsVisitor_ to count the number of files in all Java files. Please, remember to _reset()_ as soon as you finish playing with the files.

## Dealing with Threads

How good is your machine? MetricMiner can execute the visitor over many threads. This is just another configuration you set in _RepositoryMining_. The _withThreads()_ lets you configure the number of threads the framework will use to process everything.

We suggest you to use threads unless your project _checkout_ revisions. The checkout operation in Git changes the disk, so you can't actually parallelize the work.

## Using DataJoy

You have a bunch of CSV files. Do whatever you want with them. We usually upload it to a MySQL file or play it with R. If you haven't tried DataJoy yet, you should. It allows you to upload a CSV file and manipulate.

You can play with the [project here](https://www.getdatajoy.com/project/55e36c0e3b765b3001b99476).

## Advanced Configurations

### Creating your own CommitRange

(not written yet)

### Creating your own PersistenceMechanism

(not written yet)


# How to Contribute

Required: Git, Maven.

```
git clone https://github.com/mauricioaniche/metricminer2.git
cd metricminer2/test-repos
unzip *.zip
```

Then, you can:

* compile : `mvn clean compile`
* test    : `mvn test`
* eclipse : `mvn eclipse:eclipse`
* build   : `mvn clean compile assembly:single`

# License

This software is licensed under the Apache 2.0 License.

