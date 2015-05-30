/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.metricminer2.examples;

import br.com.metricminer2.SourceCodeRepositoryNavigator;
import br.com.metricminer2.Study;
import br.com.metricminer2.metric.ClassLevelMetricCalculator;
import br.com.metricminer2.metric.java8.cc.ClassLevelCyclomaticComplexityFactory;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.commitrange.Commits;

public class Example1 implements Study {

	@Override
	public void execute() {
		
		new SourceCodeRepositoryNavigator()
		.in(GitRepository.allProjectsIn("/some/path/to/many/projects/"))
		.through(Commits.all())
		.process(new ClassLevelMetricCalculator(new ClassLevelCyclomaticComplexityFactory()), 
				new CSVFile("/outputdir/", "file.csv"))
		.mine();
	}
}
