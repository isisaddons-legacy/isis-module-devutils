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

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.3"
)
public interface DeveloperUtilitiesService {

    //region > downloadMetaModel
    public static class DownloadMetaModelEvent extends ActionInteractionEvent<DeveloperUtilitiesService> {
        public DownloadMetaModelEvent(DeveloperUtilitiesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(DownloadMetaModelEvent.class)
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence="1")
    public Clob downloadMetaModel();
    //endregion

    //region > downloadLayouts
    public static class DownloadLayoutsEvent extends ActionInteractionEvent<DeveloperUtilitiesService> {
        public DownloadLayoutsEvent(DeveloperUtilitiesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Downloads a zip of the layout of all domain classes.
     */
    @ActionInteraction(DownloadLayoutsEvent.class)
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence="2")
    public Blob downloadLayouts();
    //endregion

    //region > refreshServices
    public static class RefreshServicesEvent extends ActionInteractionEvent<DeveloperUtilitiesService> {
        public RefreshServicesEvent(DeveloperUtilitiesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Rebuilds the metamodel of all registered domain services.
     */
    @ActionInteraction(RefreshServicesEvent.class)
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence="3")
    public void refreshServices();
    //endregion

    //region > downloadLayout
    public static class DownloadLayoutEvent extends ActionInteractionEvent<DeveloperUtilitiesService> {
        public DownloadLayoutEvent(DeveloperUtilitiesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Download the JSON layout of the domain object's type.
     */
    @NotInServiceMenu
    @ActionInteraction(DownloadLayoutEvent.class)
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence="98")
    public Clob downloadLayout(Object domainObject);

    //endregion

    //region > refreshLayout (deprecated)
    public static class RefreshLayoutEvent extends ActionInteractionEvent<DeveloperUtilitiesService> {
        public RefreshLayoutEvent(DeveloperUtilitiesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }


    /**
     * @deprecated - in prototype mode the Wicket viewer (at least) will automatically invalidate 
     *               the Isis metamodel whenever the object is re-rendered.
     */
    @Deprecated
    @Hidden
    @NotInServiceMenu
    @ActionInteraction(RefreshLayoutEvent.class)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence="99")
    @Prototype
    public Object refreshLayout(Object domainObject);

    //endregion

}
