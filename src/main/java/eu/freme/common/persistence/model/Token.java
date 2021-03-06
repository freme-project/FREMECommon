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
package eu.freme.common.persistence.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * @author Jan Nehring - jan.nehring@dfki.de
 */
@Entity
@Table(name = "token")
public class Token {
	@Id
	String token;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	Date creationDate;
	
	Date lastUsedDate;
	
	protected Token() {
	}

	public Token(String token, User user, Date creationDate, Date lastUsedDate) {
		super();
		this.token = token;
		this.user = user;
		this.creationDate = creationDate;
		this.lastUsedDate = lastUsedDate;
	}

	public Token(String token, User user) {
		super();
		this.token = token;
		this.user = user;
		this.creationDate = new Date();
		this.lastUsedDate = creationDate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUsedDate() {
		return lastUsedDate;
	}

	public void setLastUsedDate(Date lastUsedDate) {
		this.lastUsedDate = lastUsedDate;
	}

	
}
