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

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.3"
)
public interface DeveloperUtilitiesService {

    @ActionSemantics(Of.SAFE)
    @ActionLayout(
            prototype = true
    )
    @MemberOrder(sequence="1")
    public Clob downloadMetaModel();


    /**
     * Downloads a zip of the layout of all domain classes.
     */
    @ActionSemantics(Of.SAFE)
    @ActionLayout(
            prototype = true
    )
    @MemberOrder(sequence="2")
    public Blob downloadLayouts();

    /**
     * Rebuilds the metamodel of all registered domain services.
     */
    @ActionSemantics(Of.SAFE)
    @ActionLayout(
            prototype = true
    )
    @MemberOrder(sequence="3")
    public void refreshServices();

    /**
     * Download the JSON layout of the domain object's type.
     */
    @NotInServiceMenu
    @ActionSemantics(Of.SAFE)
    @ActionLayout(
            prototype = true
    )
    @MemberOrder(sequence="98")
    public Clob downloadLayout(Object domainObject);

    /**
     * @deprecated - in prototype mode the Wicket viewer (at least) will automatically invalidate 
     *               the Isis metamodel whenever the object is re-rendered.
     */
    @Deprecated
    @Hidden
    @NotInServiceMenu
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence="99")
    @ActionLayout(
            prototype = true
    )
    public Object refreshLayout(Object domainObject);

}
