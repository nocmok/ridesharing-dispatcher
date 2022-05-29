import * as SessionApi from "./api/SessionApi"

export class Bootstrapper {

    constructor(di) {
        this.di = di
    }

    bootstrap() {
        return SessionApi.sessions({
            filter: {
                filtering: [
                    {
                        fieldName: "terminatedAt",
                        values: [null]
                    },
                ],
                ordering: [
                    {
                        fieldName: "startedAt",
                        ascending: false,
                    }
                ],
                page: 0,
                pageSize: 10
            }
        })
            .then(response => response.sessions.map(session => session.sessionId))
            .then(activeSessionIds => {
                this.di.sessionRegistry.registerSessions(activeSessionIds)
            })
    }
}