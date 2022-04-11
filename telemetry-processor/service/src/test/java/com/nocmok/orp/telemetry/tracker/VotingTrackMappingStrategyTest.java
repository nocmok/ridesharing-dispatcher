package com.nocmok.orp.telemetry.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class VotingTrackMappingStrategyTest {

    @Autowired
    private VotingTrackMappingStrategy dumbTracker;

    @Test
    public void testMatchDenseTrack() {
        var track = new ArrayList<LatLon>();
        track.add(new LatLon(-7.4146388E7, 4.07683E7));
        track.add(new LatLon(-7.414629436708224E7, 4.076826488765584E7));
        track.add(new LatLon(-7.414620073416448E7, 4.076822977531168E7));
        track.add(new LatLon(-7.414610710124671E7, 4.076819466296752E7));
        track.add(new LatLon(-7.414601346832895E7, 4.076815955062336E7));
        track.add(new LatLon(-7.414591983541119E7, 4.07681244382792E7));
        track.add(new LatLon(-7.414582620249343E7, 4.0768089325935036E7));
        track.add(new LatLon(-7.414573256957567E7, 4.0768054213590875E7));
        track.add(new LatLon(-7.41456389366579E7, 4.0768019101246715E7));
        track.add(new LatLon(-7.41455493315949E7, 4.076797583224682E7));
        track.add(new LatLon(-7.414546453176449E7, 4.076792283235282E7));
        track.add(new LatLon(-7.414537973193409E7, 4.076786983245882E7));
        track.add(new LatLon(-7.414529493210368E7, 4.0767816832564816E7));
        track.add(new LatLon(-7.414521013227329E7, 4.0767763832670815E7));
        track.add(new LatLon(-7.414512533244288E7, 4.0767710832776815E7));
        track.add(new LatLon(-7.414504053261249E7, 4.0767657832882814E7));
        track.add(new LatLon(-7.41449557327821E7, 4.076760483298881E7));
        track.add(new LatLon(-7.414487093295169E7, 4.076755183309481E7));
        track.add(new LatLon(-7.41447859764909E7, 4.076749913278181E7));
        track.add(new LatLon(-7.414469406198789E7, 4.076745974085195E7));
        track.add(new LatLon(-7.414460214748488E7, 4.0767420348922096E7));
        track.add(new LatLon(-7.414451023298188E7, 4.076738095699224E7));
        track.add(new LatLon(-7.414441831847887E7, 4.076734156506238E7));
        track.add(new LatLon(-7.414432640397586E7, 4.0767302173132524E7));
        track.add(new LatLon(-7.414423448947287E7, 4.076726278120266E7));
        track.add(new LatLon(-7.414414257496986E7, 4.07672233892728E7));
        track.add(new LatLon(-7.4144088E7, 4.07672E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new RoadSegment("1", "0"),
                new RoadSegment("0", "4"),
                new RoadSegment("4", "14")
        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchSparseTrack() {
        var track = new ArrayList<LatLon>();
        track.add(new LatLon(-7.4146388E7, 4.07683E7));
        track.add(new LatLon(-7.414563893665795E7, 4.076801910124673E7));
        track.add(new LatLon(-7.414495573278214E7, 4.0767604832988836E7));
        track.add(new LatLon(-7.414423448947294E7, 4.076726278120269E7));
        track.add(new LatLon(-7.4144088E7, 4.07672E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new RoadSegment("1", "0"),
                new RoadSegment("0", "4"),
                new RoadSegment("4", "14")
        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchTrackOnDenseCrossroad() {
        var track = new ArrayList<LatLon>();
        track.add(new LatLon(-7.4146388E7, 4.07683E7));
        track.add(new LatLon(-7.41479774427191E7, 4.077214472135955E7));
        track.add(new LatLon(-7.41480668854382E7, 4.07721894427191E7));
        track.add(new LatLon(-7.41481563281573E7, 4.077223416407865E7));
        track.add(new LatLon(-7.414824577087641E7, 4.0772278885438204E7));
        track.add(new LatLon(-7.414833521359551E7, 4.0772323606797755E7));
        track.add(new LatLon(-7.414842465631461E7, 4.0772368328157306E7));
        track.add(new LatLon(-7.414851409903371E7, 4.077241304951686E7));
        track.add(new LatLon(-7.414860354175282E7, 4.077245777087641E7));
        track.add(new LatLon(-7.414869289768232E7, 4.077249734125816E7));
        track.add(new LatLon(-7.414878078302549E7, 4.077244963207187E7));
        track.add(new LatLon(-7.414886866836865E7, 4.0772401922885574E7));
        track.add(new LatLon(-7.414895655371182E7, 4.077235421369928E7));
        track.add(new LatLon(-7.414904443905498E7, 4.077230650451299E7));
        track.add(new LatLon(-7.414913232439815E7, 4.0772258795326695E7));
        track.add(new LatLon(-7.414922020974131E7, 4.07722110861404E7));
        track.add(new LatLon(-7.414930809508447E7, 4.077216337695411E7));
        track.add(new LatLon(-7.414939598042764E7, 4.0772115667767815E7));
        track.add(new LatLon(-7.41494838657708E7, 4.077206795858152E7));
        track.add(new LatLon(-7.414957175111397E7, 4.077202024939524E7));
        track.add(new LatLon(-7.414965963645713E7, 4.077197254020895E7));
        track.add(new LatLon(-7.41497475218003E7, 4.077192483102266E7));
        track.add(new LatLon(-7.414983540714346E7, 4.077187712183637E7));
        track.add(new LatLon(-7.414992329248662E7, 4.0771829412650086E7));
        track.add(new LatLon(-7.415001117782979E7, 4.07717817034638E7));
        track.add(new LatLon(-7.415009906317295E7, 4.077173399427751E7));
        track.add(new LatLon(-7.415018694851612E7, 4.077168628509122E7));
        track.add(new LatLon(-7.415027483385928E7, 4.0771638575904936E7));
        track.add(new LatLon(-7.415036271920244E7, 4.077159086671864E7));
        track.add(new LatLon(-7.415045060454561E7, 4.077154315753236E7));
        track.add(new LatLon(-7.415053848988877E7, 4.077149544834607E7));
        track.add(new LatLon(-7.415062637523194E7, 4.0771447739159785E7));
        track.add(new LatLon(-7.41507142605751E7, 4.077140002997349E7));
        track.add(new LatLon(-7.415080214591826E7, 4.077135232078721E7));
        track.add(new LatLon(-7.415089003126143E7, 4.077130461160092E7));
        track.add(new LatLon(-7.41509779166046E7, 4.077125690241463E7));
        track.add(new LatLon(-7.415106580194776E7, 4.077120919322834E7));
        track.add(new LatLon(-7.415115368729092E7, 4.0771161484042056E7));
        track.add(new LatLon(-7.415124157263409E7, 4.077111377485577E7));
        track.add(new LatLon(-7.415132945797725E7, 4.077106606566948E7));
        track.add(new LatLon(-7.415141734332041E7, 4.077101835648319E7));
        track.add(new LatLon(-7.415150522866358E7, 4.0770970647296906E7));
        track.add(new LatLon(-7.415159311400674E7, 4.077092293811061E7));
        track.add(new LatLon(-7.41516809993499E7, 4.077087522892433E7));
        track.add(new LatLon(-7.415176888469307E7, 4.077082751973804E7));
        track.add(new LatLon(-7.415185677003624E7, 4.0770779810551755E7));
        track.add(new LatLon(-7.41519446553794E7, 4.077073210136546E7));
        track.add(new LatLon(-7.415203254072256E7, 4.0770684392179176E7));
        track.add(new LatLon(-7.415212042606573E7, 4.077063668299289E7));
        track.add(new LatLon(-7.415220945826487E7, 4.077060858330595E7));
        track.add(new LatLon(-7.415230230593397E7, 4.077064572237358E7));
        track.add(new LatLon(-7.415239515360306E7, 4.077068286144122E7));
        track.add(new LatLon(-7.415248800127216E7, 4.077072000050885E7));
        track.add(new LatLon(-7.415258084894124E7, 4.0770757139576495E7));
        track.add(new LatLon(-7.415267369661033E7, 4.077079427864413E7));
        track.add(new LatLon(-7.4152688E7, 4.07708E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new RoadSegment("31", "48"),
                new RoadSegment("48", "74"),
                new RoadSegment("74", "72")
        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchTrackWithRandomNoise() {
        var track = new ArrayList<LatLon>();
        track.add(new LatLon(-7.414638800000258E7,4.076829999997852E7));
        track.add(new LatLon(-7.414797744275127E7,4.0772144721321926E7));
        track.add(new LatLon(-7.414806688546272E7,4.077218944270186E7));
        track.add(new LatLon(-7.414815632822381E7,4.077223416413715E7));
        track.add(new LatLon(-7.4148245770815E7,4.077227888537994E7));
        track.add(new LatLon(-7.414833521359731E7,4.077232360680921E7));
        track.add(new LatLon(-7.414842465631334E7,4.077236832815897E7));
        track.add(new LatLon(-7.414851409911674E7,4.077241304949175E7));
        track.add(new LatLon(-7.414860354170828E7,4.0772457770910725E7));
        track.add(new LatLon(-7.414869289774145E7,4.077249734129527E7));
        track.add(new LatLon(-7.414878078298174E7,4.077244963206931E7));
        track.add(new LatLon(-7.41488686683702E7,4.077240192281966E7));
        track.add(new LatLon(-7.414895655368085E7,4.077235421363048E7));
        track.add(new LatLon(-7.414904443913473E7,4.077230650449948E7));
        track.add(new LatLon(-7.414913232439317E7,4.077225879533227E7));
        track.add(new LatLon(-7.414922020967694E7,4.0772211086131684E7));
        track.add(new LatLon(-7.414930809508727E7,4.077216337694154E7));
        track.add(new LatLon(-7.414939598042803E7,4.0772115667767905E7));
        track.add(new LatLon(-7.414948386574258E7,4.077206795859513E7));
        track.add(new LatLon(-7.414957175115487E7,4.077202024946081E7));
        track.add(new LatLon(-7.414965963638051E7,4.077197254019511E7));
        track.add(new LatLon(-7.414974752181253E7,4.07719248310122E7));
        track.add(new LatLon(-7.41498354071279E7,4.077187712191163E7));
        track.add(new LatLon(-7.414992329242449E7,4.077182941265387E7));
        track.add(new LatLon(-7.415001117782123E7,4.0771781703400284E7));
        track.add(new LatLon(-7.415009906315596E7,4.077173399434585E7));
        track.add(new LatLon(-7.415018694851491E7,4.077168628509079E7));
        track.add(new LatLon(-7.415027483391972E7,4.07716385759523E7));
        track.add(new LatLon(-7.415036271918239E7,4.0771590866631575E7));
        track.add(new LatLon(-7.415045060453424E7,4.077154315752209E7));
        track.add(new LatLon(-7.415053848982547E7,4.077149544833069E7));
        track.add(new LatLon(-7.415062637526383E7,4.077144773909485E7));
        track.add(new LatLon(-7.415071426061387E7,4.0771400029988445E7));
        track.add(new LatLon(-7.41508021458911E7,4.077135232075117E7));
        track.add(new LatLon(-7.415089003117438E7,4.077130461159651E7));
        track.add(new LatLon(-7.415097791660716E7,4.077125690241802E7));
        track.add(new LatLon(-7.41510658019454E7,4.077120919322948E7));
        track.add(new LatLon(-7.415115368737787E7,4.077116148405101E7));
        track.add(new LatLon(-7.415124157256523E7,4.0771113774851054E7));
        track.add(new LatLon(-7.415132945800023E7,4.0771066065669276E7));
        track.add(new LatLon(-7.415141734334812E7,4.077101835655427E7));
        track.add(new LatLon(-7.415150522866271E7,4.0770970647297196E7));
        track.add(new LatLon(-7.415159311401148E7,4.077092293811681E7));
        track.add(new LatLon(-7.415168099934971E7,4.077087522892406E7));
        track.add(new LatLon(-7.415176888471007E7,4.077082751978272E7));
        track.add(new LatLon(-7.415185677001448E7,4.077077981053958E7));
        track.add(new LatLon(-7.415194465534079E7,4.07707321013265E7));
        track.add(new LatLon(-7.415203254071921E7,4.0770684392177E7));
        track.add(new LatLon(-7.415212042609273E7,4.077063668295729E7));
        track.add(new LatLon(-7.415220945831166E7,4.077060858330397E7));
        track.add(new LatLon(-7.415230230596265E7,4.077064572240556E7));
        track.add(new LatLon(-7.415239515361135E7,4.07706828614134E7));
        track.add(new LatLon(-7.415248800120927E7,4.0770720000513464E7));
        track.add(new LatLon(-7.415258084894283E7,4.0770757139521904E7));
        track.add(new LatLon(-7.415267369660705E7,4.077079427867896E7));
        track.add(new LatLon(-7.415268799998721E7,4.077079999999205E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new RoadSegment("31", "48"),
                new RoadSegment("48", "74"),
                new RoadSegment("74", "72")
        );

        assertEquals(expectedLog, roadLog);
    }
}
