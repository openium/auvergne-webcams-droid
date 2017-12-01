package fr.openium.auvergnewebcams.ext

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject

/**
 * Created by t.coulange on 25/03/16.
 */
fun Realm.insertInTransaction(model: RealmModel) {
    executeTransaction {
        insertOrUpdate(model)
    }
}

fun Realm.insertInTransaction(models: Collection<RealmModel>) {
    executeTransaction {
        insertOrUpdate(models)
    }
}

fun RealmModel.deleteFromRealm() {
    RealmObject.deleteFromRealm(this)
}