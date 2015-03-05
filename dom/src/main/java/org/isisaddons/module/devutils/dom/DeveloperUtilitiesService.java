/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.devutils.dom;

import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

public interface DeveloperUtilitiesService {

    public Clob downloadMetaModel();

    public Blob downloadLayouts();

    public void refreshServices();

    public Clob downloadLayout(Object domainObject);

    public Object refreshLayout(Object domainObject);

}
