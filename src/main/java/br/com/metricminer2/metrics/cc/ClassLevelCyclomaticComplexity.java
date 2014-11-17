package br.com.metricminer2.metrics.cc;

import java.io.ByteArrayInputStream;

import br.com.metricminer2.metrics.ClassLevelCodeMetric;
import br.com.metricminer2.metrics.MetricException;
import br.com.metricminer2.metrics.antlr.java8.Java8AntLRVisitor;

public class ClassLevelCyclomaticComplexity implements ClassLevelCodeMetric {

	private CCListener visitor;
	
	@Override
	public double calculate(String sourceCode) {
		
		try {
			visitor = new CCListener();
			new Java8AntLRVisitor().visit(visitor, new ByteArrayInputStream(sourceCode.getBytes()));
			
			return visitor.getCc();
		} catch (Throwable t) {
			throw new MetricException(this, sourceCode, t);
		}

	}

	@Override
	public String getName() {
		return "class-cc";
	}

	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}

	@Override
	public double threshold() {
		return 20;
	}

	@Override
	public String getPrettyName() {
		return "complexity";
	}

}
