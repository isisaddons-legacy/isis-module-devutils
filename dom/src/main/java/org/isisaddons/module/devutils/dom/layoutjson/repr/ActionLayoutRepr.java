/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.isisaddons.module.devutils.dom.layoutjson.repr;


import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Where;

public class ActionLayoutRepr {
    public BookmarkPolicy bookmarking;
    public String cssClass;
    public String cssClassFa;
    public ActionLayout.CssClassFaPosition cssClassFaPosition;
    public String describedAs;
    public Where hidden;
    public String named;
    // this is missing from @ActionLayout in 1.8.0
    //public boolean namedEscaped = true;
    public ActionLayout.Position position;
    public Contributed contributed;
}
