#!/bin/bash
# shellcheck disable=SC2016
telemetry_log=('echo "{\"sessionId\":\"1\",\"lat\":\"-7.4146388E7\",\"lon\":\"4.07683E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41479774427191E7\",\"lon\":\"4.077214472135955E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41480668854382E7\",\"lon\":\"4.07721894427191E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41481563281573E7\",\"lon\":\"4.077223416407865E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414824577087641E7\",\"lon\":\"4.0772278885438204E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414833521359551E7\",\"lon\":\"4.0772323606797755E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414842465631461E7\",\"lon\":\"4.0772368328157306E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414851409903371E7\",\"lon\":\"4.077241304951686E7\",\"accuracy\":\"10\",\"recor§dedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414860354175282E7\",\"lon\":\"4.077245777087641E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414869289768232E7\",\"lon\":\"4.077249734125816E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414878078302549E7\",\"lon\":\"4.077244963207187E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414886866836865E7\",\"lon\":\"4.0772401922885574E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414895655371182E7\",\"lon\":\"4.077235421369928E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414904443905498E7\",\"lon\":\"4.077230650451299E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414913232439815E7\",\"lon\":\"4.0772258795326695E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414922020974131E7\",\"lon\":\"4.07722110861404E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414930809508447E7\",\"lon\":\"4.077216337695411E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414939598042764E7\",\"lon\":\"4.0772115667767815E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41494838657708E7\",\"lon\":\"4.077206795858152E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414957175111397E7\",\"lon\":\"4.077202024939524E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414965963645713E7\",\"lon\":\"4.077197254020895E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41497475218003E7\",\"lon\":\"4.077192483102266E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414983540714346E7\",\"lon\":\"4.077187712183637E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.414992329248662E7\",\"lon\":\"4.0771829412650086E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415001117782979E7\",\"lon\":\"4.07717817034638E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415009906317295E7\",\"lon\":\"4.077173399427751E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415018694851612E7\",\"lon\":\"4.077168628509122E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415027483385928E7\",\"lon\":\"4.0771638575904936E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415036271920244E7\",\"lon\":\"4.077159086671864E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415045060454561E7\",\"lon\":\"4.077154315753236E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415053848988877E7\",\"lon\":\"4.077149544834607E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415062637523194E7\",\"lon\":\"4.0771447739159785E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41507142605751E7\",\"lon\":\"4.077140002997349E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415080214591826E7\",\"lon\":\"4.077135232078721E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415089003126143E7\",\"lon\":\"4.077130461160092E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41509779166046E7\",\"lon\":\"4.077125690241463E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415106580194776E7\",\"lon\":\"4.077120919322834E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415115368729092E7\",\"lon\":\"4.0771161484042056E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415124157263409E7\",\"lon\":\"4.077111377485577E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415132945797725E7\",\"lon\":\"4.077106606566948E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415141734332041E7\",\"lon\":\"4.077101835648319E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415150522866358E7\",\"lon\":\"4.0770970647296906E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415159311400674E7\",\"lon\":\"4.077092293811061E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41516809993499E7\",\"lon\":\"4.077087522892433E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415176888469307E7\",\"lon\":\"4.077082751973804E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415185677003624E7\",\"lon\":\"4.0770779810551755E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.41519446553794E7\",\"lon\":\"4.077073210136546E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415203254072256E7\",\"lon\":\"4.0770684392179176E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415212042606573E7\",\"lon\":\"4.077063668299289E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415220945826487E7\",\"lon\":\"4.077060858330595E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415230230593397E7\",\"lon\":\"4.077064572237358E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415239515360306E7\",\"lon\":\"4.077068286144122E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415248800127216E7\",\"lon\":\"4.077072000050885E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415258084894124E7\",\"lon\":\"4.0770757139576495E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.415267369661033E7\",\"lon\":\"4.077079427864413E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"'
              'echo "{\"sessionId\":\"1\",\"lat\":\"-7.4152688E7\",\"lon\":\"4.07708E7\",\"accuracy\":\"10\",\"recordedAt\":\"$(gdate -d "-3 hours" +"%Y-%m-%dT%H:%M:%S.000000Z")\"}"')

for telemetry in "${telemetry_log[@]}"; do
  echo "sending telemetry $(eval "$telemetry")"
  eval "$telemetry" | kcat -P -b localhost:29092 -k 1 -t orp.telemetry -H __TypeId__=com.nocmok.orp.kafka.orp_telemetry.VehicleTelemetryMessage
  sleep 5
done