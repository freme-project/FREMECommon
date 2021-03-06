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
package eu.freme.common.persistence.dao;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.freme.common.exception.OwnedResourceNotFoundException;
import eu.freme.common.persistence.model.OwnedResource;
import eu.freme.common.persistence.model.User;
import eu.freme.common.persistence.repository.OwnedResourceRepository;
import eu.freme.common.persistence.tools.AccessLevelHelper;

/**
 * Created by Arne on 18.09.2015.
 */
public abstract class OwnedResourceDAO<Entity extends OwnedResource>  extends DAO<OwnedResourceRepository<Entity>, Entity>{

    @Autowired
    AbstractAccessDecisionManager decisionManager;

    @Autowired
    AccessLevelHelper accessLevelHelper;

    public abstract String tableName();

    @Override
	public void delete(Entity entity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        decisionManager.decide(authentication, entity, accessLevelHelper.writeAccess());
        super.delete(entity);
    }

    @Override
	public Entity save(Entity entity){
        if(entity.getOwner() == null) {
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();
            if(authentication instanceof AnonymousAuthenticationToken)
                throw new AccessDeniedException("Could not set current user as owner of created resource ("+tableName()+"): The anonymous user can not own any resource. You have to be logged in to create a resource.");
            entity.setOwner((User) authentication.getPrincipal());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        decisionManager.decide(authentication, entity, accessLevelHelper.writeAccess());
        entity.preSave();
        return super.save(entity);
    }

    public Entity findOneByIdentifier(String identifier){
        Entity result = findOneByIdentifierUnsecured(identifier);
        if(result==null)
            throw new OwnedResourceNotFoundException("Can not find "+tableName()+" with "+getIdentifierName()+"='"+identifier+"'");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        decisionManager.decide(authentication, result, accessLevelHelper.readAccess());
        result.postFetch();
        return result;
    }

    public Entity findOneByIdentifierUnsecured(String identifier){
        return repository.findOneById(Integer.parseInt(identifier));
    }

    public String getIdentifierName(){
        return "id";
    }

    public Entity updateOwner(Entity entity, User newOwner){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        decisionManager.decide(authentication, entity, accessLevelHelper.writeAccess());
        entity.setOwner(newOwner);
        return super.save(entity);
    }

    @SuppressWarnings("unchecked")
	public List<Entity> findAllReadAccessible(){
        if(repository.count()==0)
            return new ArrayList<>(0);

        String tableName = tableName();
        String entityName = tableName.toLowerCase();
        String queryString;
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken) {
            logger.debug("Find owned resources as ANONYMOUS USER");
            queryString = "select " + entityName + " from " + tableName + " " + entityName + " where " + entityName + ".visibility = " + OwnedResource.Visibility.PUBLIC.ordinal()+" order by "+getIdentifierName(); //
        }else {
            User authUser = (User) authentication.getPrincipal();
            if(authUser.getRole().equals(User.roleAdmin)) {
                queryString = "select " + entityName + " from " + tableName + " " + entityName + " order by "+getIdentifierName();
            }else {
                queryString = "select " + entityName + " from " + tableName + " " + entityName + " where " + entityName + ".owner.name = '" + authUser.getName() + "' or " + entityName + ".visibility = " + OwnedResource.Visibility.PUBLIC.ordinal() + " order by "+getIdentifierName(); //
            }
        }
        List<Entity> result = entityManager.createQuery(queryString).getResultList();
        for(Entity entity: result){
            entity.postFetch();
        }
        return result;
    }

    public boolean hasReadAccess(Entity entity){
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        try {
            decisionManager.decide(authentication, entity, accessLevelHelper.readAccess());
        }catch (AccessDeniedException e){
            return false;
        }
        return true;
    }

    public boolean hasWriteAccess(Entity entity){
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        try {
            decisionManager.decide(authentication, entity, accessLevelHelper.writeAccess());
        }catch (AccessDeniedException e){
            return false;
        }
        return true;
    }

}
