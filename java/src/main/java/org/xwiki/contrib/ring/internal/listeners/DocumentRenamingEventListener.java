/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.xwiki.contrib.ring.internal.listeners;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import aek.ring.RingException;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.event.Event;
import org.xwiki.refactoring.event.DocumentRenamingEvent;

/**
 * Listener updating the inverse vertices of an ringSet destination when the destination gets updated, or rings when a
 * relation page gets updated.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named(DocumentRenamingEventListener.NAME)
public class DocumentRenamingEventListener extends BaseXWikiRingEventListener
{
    /**
     * Listener's name.
     */
    public static final String NAME = "ringSet.page.rename";

    /**
     * Default constructor.
     */
    public DocumentRenamingEventListener()
    {
        super(NAME, new DocumentRenamingEvent());
    }

    public void onEvent(Event event, Object source, Object data)
    {
        DocumentReference originalReference = ((DocumentRenamingEvent) event).getSourceReference();
        DocumentReference newReference = ((DocumentRenamingEvent) event).getTargetReference();
        try {
            // Update all rings using the reference as a destination
            ringSet.updateRingsTo(originalReference, newReference);
            // Update all rings using the reference as a relation, if any
            ringSet.updateRingsWith(originalReference, newReference);
            // No need to update rings origin since it's not stored as such at the moment since all rings
            // are stored as objects attached to their origin vertex document.
        } catch (RingException e) {
            // FIXME: error handling
            logger.error("Error while updating inverse relations of document {}.",
                    serializer.serialize(originalReference), e);
        }
    }

}
