/**
 * Copyright © 2015 Agro-Know, Deutsches Forschungszentrum für Künstliche Intelligenz, iMinds,
 * Institut für Angewandte Informatik e. V. an der Universität Leipzig,
 * Istituto Superiore Mario Boella, Tilde, Vistatec, WRIPL (http://freme-project.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.freme.common.conversion.rdf;

import com.hp.hpl.jena.rdf.model.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Jan Nehring - jan.nehring@dfki.de
 */
public class JenaRDFConverterTest {


	@Test
	public void testPlaintextToRDF() {

		JenaRDFConversionService converter = new JenaRDFConversionService();

		String plaintext = "hello world";
		String language = "en";
		Model model = ModelFactory.createDefaultModel();
		converter.plaintextToRDF(model, plaintext, language, RDFConstants.fremePrefix, RDFConstants.nifVersion2_0);

		assertTrue(countStatements(model.listStatements()) == 6);

		Property isString = model.createProperty(RDFConstants.nifPrefix
				+ "isString");
		assertTrue(countStatements(model.listStatements((Resource) null,
				isString, (RDFNode) null)) == 1);

		Property beginIndex = model.createProperty(RDFConstants.nifPrefix
				+ "beginIndex");
		assertTrue(countStatements(model.listStatements((Resource) null,
				beginIndex, (RDFNode) null)) == 1);

		Property endIndex = model.createProperty(RDFConstants.nifPrefix
				+ "endIndex");
		assertTrue(countStatements(model.listStatements((Resource) null,
				endIndex, (RDFNode) null)) == 1);

		model = ModelFactory.createDefaultModel();
		Resource res = converter.plaintextToRDF(model, plaintext, null, RDFConstants.fremePrefix, RDFConstants.nifVersion2_0);

		assertTrue(countStatements(model.listStatements((Resource) null,
				isString, (RDFNode) null)) == 1);
		assertTrue(countStatements(model.listStatements((Resource) null,
				beginIndex, (RDFNode) null)) == 1);
		assertTrue(countStatements(model.listStatements((Resource) null,
				endIndex, (RDFNode) null)) == 1);

		assertTrue(res.getProperty(isString).getLiteral().getLanguage().trim()
				.length() == 0);

	}

	@Test
	public void testSerializeRDF() throws Exception {
		JenaRDFConversionService converter = new JenaRDFConversionService();
		Model model = ModelFactory.createDefaultModel();

		converter.plaintextToRDF(model, "test", "en", RDFConstants.fremePrefix, RDFConstants.nifVersion2_0);
		String str = converter.serializeRDF(model,
				RDFConstants.TURTLE);
		assertTrue(str.length() > 0);

		str = converter.serializeRDF(model,
				RDFConstants.JSON_LD);
		assertTrue(str.length() > 0);
	}

	private int countStatements(StmtIterator itr) {
		int count = 0;
		while (itr.hasNext()) {
			count++;
			itr.next();
		}
		return count;
	}

	private String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder bldr = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			bldr.append(line);
			bldr.append("\n");
		}
		reader.close();
		return bldr.toString();
	}

	@Test
	public void testUnserializeRDF() throws Exception {
		JenaRDFConversionService converter = new JenaRDFConversionService();

		String rdf = readFile("src/test/resources/rdftest/test.turtle");
		converter.unserializeRDF(rdf, RDFConstants.TURTLE);

		rdf = readFile("src/test/resources/rdftest/test2.turtle");
		converter.unserializeRDF(rdf, RDFConstants.TURTLE);

		rdf = readFile("src/test/resources/rdftest/test.jsonld");
		converter.unserializeRDF(rdf, RDFConstants.JSON_LD);
	}
	
	@Test
	public void testExtractPlaintext() throws Exception{
		
		JenaRDFConversionService converter = new JenaRDFConversionService();
		
		String rdf = readFile("src/test/resources/rdftest/test-plaintext.ttl");
		Model model = converter.unserializeRDF(rdf, RDFConstants.TURTLE);
		Statement plaintext = converter.extractFirstPlaintext(model);
		assertTrue(plaintext == null);
		
		rdf = readFile("src/test/resources/rdftest/test-plaintext-2.ttl");
		model = converter.unserializeRDF(rdf, RDFConstants.TURTLE);
		plaintext = converter.extractFirstPlaintext(model);
		assertTrue(plaintext.getObject().asLiteral().getString().equals("hello world"));
	}
}
