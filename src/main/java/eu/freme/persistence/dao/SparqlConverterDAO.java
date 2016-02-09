package eu.freme.persistence.dao;

import eu.freme.persistence.model.SparqlConverter;
import eu.freme.persistence.repository.SparqlConverterRepository;

import org.springframework.stereotype.Component;

/**
 * Created by Arne on 11.12.2015.
 */

@Component
public class SparqlConverterDAO extends OwnedResourceDAO<SparqlConverter> {

    @Override
    public String className() {
        return SparqlConverter.class.getSimpleName();
    }

    @Override
    public SparqlConverter findOneByIdentifierUnsecured(String identifier){
        return ((SparqlConverterRepository)repository).findOneByName(identifier);
    }

}