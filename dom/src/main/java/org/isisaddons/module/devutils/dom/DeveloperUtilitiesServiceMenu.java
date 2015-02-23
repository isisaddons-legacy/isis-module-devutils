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

import javax.inject.Inject;
import org.isisaddons.module.devutils.DevUtilsModule;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.3"
)
public class DeveloperUtilitiesServiceMenu {

    public static abstract class ActionDomainEvent extends DevUtilsModule.ActionDomainEvent<DeveloperUtilitiesServiceMenu> {
        public ActionDomainEvent(final DeveloperUtilitiesServiceMenu source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    public static class DownloadMetaModelEvent extends ActionDomainEvent {
        public DownloadMetaModelEvent(final DeveloperUtilitiesServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = DownloadMetaModelEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence="1")
    public Clob downloadMetaModel() {
        return developerUtilitiesService.downloadMetaModel();
    }

    // //////////////////////////////////////

    public static class RefreshServicesEvent extends ActionDomainEvent {
        public RefreshServicesEvent(final DeveloperUtilitiesServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = RefreshServicesEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence="3")
    public void refreshServices() {
        developerUtilitiesService.refreshServices();
    }

    // //////////////////////////////////////

    public static class DownloadLayoutsEvent extends ActionDomainEvent {
        public DownloadLayoutsEvent(final DeveloperUtilitiesServiceMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = DownloadLayoutsEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence="2")
    public Blob downloadLayouts() {
        return developerUtilitiesService.downloadLayouts();
    }


    // //////////////////////////////////////

    @Inject
    private DeveloperUtilitiesService developerUtilitiesService;

}
