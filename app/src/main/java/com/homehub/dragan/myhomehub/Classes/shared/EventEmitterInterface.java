package com.homehub.dragan.myhomehub.Classes.shared;

import com.homehub.dragan.myhomehub.Classes.model.rest.RxPayload;

import io.reactivex.subjects.Subject;

public interface EventEmitterInterface {
    Subject<RxPayload> getEventSubject();
}
