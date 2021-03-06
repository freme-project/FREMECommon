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
package eu.freme.common.conversion.etranslate;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Jan Nehring - jan.nehring@dfki.de
 */
public interface TranslationConversionService {

	/*
	 * Enrich an existing RDF model with translation information.
	 * 
	 * @param translation
	 *            The translated string
	 * @param source
	 *            The resource that is enriched
	 * @param targetLanguage
	 *            The target language identifier (e.g. "en" or "de")
	 * @return the newly generated resource
	 */
	public Resource addTranslation(String translation, Resource source,
			String targetLanguage);

	/*
	 * Returns the first Reosurce in a model that has a property
	 * nif:isString. Returns null if no such literal exists in the model.
	 */
	public Resource extractTextToTranslate(Model resource);
}
