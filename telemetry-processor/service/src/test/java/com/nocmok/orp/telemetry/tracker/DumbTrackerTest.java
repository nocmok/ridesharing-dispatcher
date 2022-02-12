package com.nocmok.orp.telemetry.tracker;

import com.nocmok.orp.core_api.GCS;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.GraphRoad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DumbTrackerTest {

    @Autowired
    private DumbTracker dumbTracker;

    @Test
    public void testGetBinding() {
        var road = new GraphRoad(
                new GraphNode(0, new GCS(-7.4145588E7, 4.0768E7)),
                new GraphNode(1, new GCS(-7.4146388E7, 4.07683E7))
        );
        assertEquals(0d, dumbTracker.getBinding(road, new GCS(-7.4145588E7, 4.0768E7)).getProgress(), 1e-5);
        assertEquals(0.5, dumbTracker.getBinding(road, new GCS((-7.4145588E7 + -7.4146388E7) / 2, (4.0768E7 + 4.07683E7) / 2)).getProgress(), 1e-5);
        assertEquals(1d, dumbTracker.getBinding(road, new GCS(-7.4146388E7, 4.07683E7)).getProgress(), 1e-5);
    }

    @Test
    public void testMatchDenseTrack() {
        var track = new ArrayList<GCS>();
        track.add(new GCS(-7.4146388E7, 4.07683E7));
        track.add(new GCS(-7.414629436708224E7,4.076826488765584E7));
        track.add(new GCS(-7.414620073416448E7,4.076822977531168E7));
        track.add(new GCS(-7.414610710124671E7,4.076819466296752E7));
        track.add(new GCS(-7.414601346832895E7,4.076815955062336E7));
        track.add(new GCS(-7.414591983541119E7,4.07681244382792E7));
        track.add(new GCS(-7.414582620249343E7,4.0768089325935036E7));
        track.add(new GCS(-7.414573256957567E7,4.0768054213590875E7));
        track.add(new GCS(-7.41456389366579E7,4.0768019101246715E7));
        track.add(new GCS(-7.41455493315949E7,4.076797583224682E7));
        track.add(new GCS(-7.414546453176449E7,4.076792283235282E7));
        track.add(new GCS(-7.414537973193409E7,4.076786983245882E7));
        track.add(new GCS(-7.414529493210368E7,4.0767816832564816E7));
        track.add(new GCS(-7.414521013227329E7,4.0767763832670815E7));
        track.add(new GCS(-7.414512533244288E7,4.0767710832776815E7));
        track.add(new GCS(-7.414504053261249E7,4.0767657832882814E7));
        track.add(new GCS(-7.41449557327821E7,4.076760483298881E7));
        track.add(new GCS(-7.414487093295169E7,4.076755183309481E7));
        track.add(new GCS(-7.41447859764909E7,4.076749913278181E7));
        track.add(new GCS(-7.414469406198789E7,4.076745974085195E7));
        track.add(new GCS(-7.414460214748488E7,4.0767420348922096E7));
        track.add(new GCS(-7.414451023298188E7,4.076738095699224E7));
        track.add(new GCS(-7.414441831847887E7,4.076734156506238E7));
        track.add(new GCS(-7.414432640397586E7,4.0767302173132524E7));
        track.add(new GCS(-7.414423448947287E7,4.076726278120266E7));
        track.add(new GCS(-7.414414257496986E7,4.07672233892728E7));
        track.add(new GCS(-7.4144088E7,4.07672E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new GraphRoad(new GraphNode(1, null), new GraphNode(0, null)),
                new GraphRoad(new GraphNode(0, null), new GraphNode(4, null)),
                new GraphRoad(new GraphNode(4, null), new GraphNode(14, null))
        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchSparseTrack() {
        var track = new ArrayList<GCS>();
        track.add(new GCS(-7.4146388E7, 4.07683E7));
        track.add(new GCS(-7.414563893665795E7,4.076801910124673E7));
        track.add(new GCS(-7.414495573278214E7,4.0767604832988836E7));
        track.add(new GCS(-7.414423448947294E7,4.076726278120269E7));
        track.add(new GCS(-7.4144088E7,4.07672E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new GraphRoad(new GraphNode(1, null), new GraphNode(0, null)),
                new GraphRoad(new GraphNode(0, null), new GraphNode(4, null)),
                new GraphRoad(new GraphNode(4, null), new GraphNode(14, null))
        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchTrackOnDenseCrossroad() {
        var track = new ArrayList<GCS>();
        track.add(new GCS(-7.4146388E7,4.07683E7));
        track.add(new GCS(-7.41479774427191E7,4.077214472135955E7));
        track.add(new GCS(-7.41480668854382E7,4.07721894427191E7));
        track.add(new GCS(-7.41481563281573E7,4.077223416407865E7));
        track.add(new GCS(-7.414824577087641E7,4.0772278885438204E7));
        track.add(new GCS(-7.414833521359551E7,4.0772323606797755E7));
        track.add(new GCS(-7.414842465631461E7,4.0772368328157306E7));
        track.add(new GCS(-7.414851409903371E7,4.077241304951686E7));
        track.add(new GCS(-7.414860354175282E7,4.077245777087641E7));
        track.add(new GCS(-7.414869289768232E7,4.077249734125816E7));
        track.add(new GCS(-7.414878078302549E7,4.077244963207187E7));
        track.add(new GCS(-7.414886866836865E7,4.0772401922885574E7));
        track.add(new GCS(-7.414895655371182E7,4.077235421369928E7));
        track.add(new GCS(-7.414904443905498E7,4.077230650451299E7));
        track.add(new GCS(-7.414913232439815E7,4.0772258795326695E7));
        track.add(new GCS(-7.414922020974131E7,4.07722110861404E7));
        track.add(new GCS(-7.414930809508447E7,4.077216337695411E7));
        track.add(new GCS(-7.414939598042764E7,4.0772115667767815E7));
        track.add(new GCS(-7.41494838657708E7,4.077206795858152E7));
        track.add(new GCS(-7.414957175111397E7,4.077202024939524E7));
        track.add(new GCS(-7.414965963645713E7,4.077197254020895E7));
        track.add(new GCS(-7.41497475218003E7,4.077192483102266E7));
        track.add(new GCS(-7.414983540714346E7,4.077187712183637E7));
        track.add(new GCS(-7.414992329248662E7,4.0771829412650086E7));
        track.add(new GCS(-7.415001117782979E7,4.07717817034638E7));
        track.add(new GCS(-7.415009906317295E7,4.077173399427751E7));
        track.add(new GCS(-7.415018694851612E7,4.077168628509122E7));
        track.add(new GCS(-7.415027483385928E7,4.0771638575904936E7));
        track.add(new GCS(-7.415036271920244E7,4.077159086671864E7));
        track.add(new GCS(-7.415045060454561E7,4.077154315753236E7));
        track.add(new GCS(-7.415053848988877E7,4.077149544834607E7));
        track.add(new GCS(-7.415062637523194E7,4.0771447739159785E7));
        track.add(new GCS(-7.41507142605751E7,4.077140002997349E7));
        track.add(new GCS(-7.415080214591826E7,4.077135232078721E7));
        track.add(new GCS(-7.415089003126143E7,4.077130461160092E7));
        track.add(new GCS(-7.41509779166046E7,4.077125690241463E7));
        track.add(new GCS(-7.415106580194776E7,4.077120919322834E7));
        track.add(new GCS(-7.415115368729092E7,4.0771161484042056E7));
        track.add(new GCS(-7.415124157263409E7,4.077111377485577E7));
        track.add(new GCS(-7.415132945797725E7,4.077106606566948E7));
        track.add(new GCS(-7.415141734332041E7,4.077101835648319E7));
        track.add(new GCS(-7.415150522866358E7,4.0770970647296906E7));
        track.add(new GCS(-7.415159311400674E7,4.077092293811061E7));
        track.add(new GCS(-7.41516809993499E7,4.077087522892433E7));
        track.add(new GCS(-7.415176888469307E7,4.077082751973804E7));
        track.add(new GCS(-7.415185677003624E7,4.0770779810551755E7));
        track.add(new GCS(-7.41519446553794E7,4.077073210136546E7));
        track.add(new GCS(-7.415203254072256E7,4.0770684392179176E7));
        track.add(new GCS(-7.415212042606573E7,4.077063668299289E7));
        track.add(new GCS(-7.415220945826487E7,4.077060858330595E7));
        track.add(new GCS(-7.415230230593397E7,4.077064572237358E7));
        track.add(new GCS(-7.415239515360306E7,4.077068286144122E7));
        track.add(new GCS(-7.415248800127216E7,4.077072000050885E7));
        track.add(new GCS(-7.415258084894124E7,4.0770757139576495E7));
        track.add(new GCS(-7.415267369661033E7,4.077079427864413E7));
        track.add(new GCS(-7.4152688E7,4.07708E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new GraphRoad(new GraphNode(31, null), new GraphNode(48, null)),
                new GraphRoad(new GraphNode(48, null), new GraphNode(74, null)),
                new GraphRoad(new GraphNode(74, null), new GraphNode(72, null))

        );

        assertEquals(expectedLog, roadLog);
    }

    @Test
    public void testMatchTrackWithRandomNoise() {
        var track = new ArrayList<GCS>();
        track.add(new GCS(-7.414638828695542E7,4.07682976118138E7));
        track.add(new GCS(-7.414798101969261E7,4.077214053765901E7));
        track.add(new GCS(-7.41480696111184E7,4.07721875259203E7));
        track.add(new GCS(-7.414816372329924E7,4.0772240668166734E7));
        track.add(new GCS(-7.414823894265082E7,4.077227240698644E7));
        track.add(new GCS(-7.414833541486683E7,4.077232487995815E7));
        track.add(new GCS(-7.414842451574153E7,4.07723685131985E7));
        track.add(new GCS(-7.41485233309733E7,4.077241025760317E7));
        track.add(new GCS(-7.414859858989136E7,4.0772461586615615E7));
        track.add(new GCS(-7.414869947303113E7,4.077250146810508E7));
        track.add(new GCS(-7.414877591897222E7,4.077244934822243E7));
        track.add(new GCS(-7.414886884149337E7,4.077239459340922E7));
        track.add(new GCS(-7.41489531113089E7,4.077234656362973E7));
        track.add(new GCS(-7.414905330615358E7,4.077230500287761E7));
        track.add(new GCS(-7.414913177095856E7,4.077225941471415E7));
        track.add(new GCS(-7.414921305152743E7,4.0772210116627805E7));
        track.add(new GCS(-7.41493084072377E7,4.0772161979181506E7));
        track.add(new GCS(-7.414939602407423E7,4.0772115678113736E7));
        track.add(new GCS(-7.414948072727576E7,4.077206947169571E7));
        track.add(new GCS(-7.414957629954877E7,4.077202754056174E7));
        track.add(new GCS(-7.414965111697944E7,4.077197100069838E7));
        track.add(new GCS(-7.414974888224305E7,4.077192366789526E7));
        track.add(new GCS(-7.414983367805403E7,4.077188548993541E7));
        track.add(new GCS(-7.414991638234015E7,4.077182983316935E7));
        track.add(new GCS(-7.415001022629632E7,4.07717746403964E7));
        track.add(new GCS(-7.415009717463389E7,4.0771741594067305E7));
        track.add(new GCS(-7.415018681446831E7,4.077168623714785E7));
        track.add(new GCS(-7.41502815538587E7,4.077164384254301E7));
        track.add(new GCS(-7.415036048855306E7,4.07715811856621E7));
        track.add(new GCS(-7.415044934067324E7,4.0771542016240485E7));
        track.add(new GCS(-7.41505314516673E7,4.077149373844315E7));
        track.add(new GCS(-7.415062992070027E7,4.0771440518712915E7));
        track.add(new GCS(-7.415071857203719E7,4.0771401692449264E7));
        track.add(new GCS(-7.415079912454963E7,4.077134831389898E7));
        track.add(new GCS(-7.415088035097973E7,4.077130412112973E7));
        track.add(new GCS(-7.415097820183574E7,4.077125727930909E7));
        track.add(new GCS(-7.415106553985311E7,4.077120931984241E7));
        track.add(new GCS(-7.415116335567635E7,4.077116247954536E7));
        track.add(new GCS(-7.415123391614765E7,4.07711132504957E7));
        track.add(new GCS(-7.415133201313187E7,4.0771066043235116E7));
        track.add(new GCS(-7.415142042428869E7,4.077102626000235E7));
        track.add(new GCS(-7.415150513230938E7,4.0770970680005565E7));
        track.add(new GCS(-7.415159364067599E7,4.077092362728769E7));
        track.add(new GCS(-7.415168097767943E7,4.077087519921992E7));
        track.add(new GCS(-7.415177077607462E7,4.077083248798135E7));
        track.add(new GCS(-7.415185435084362E7,4.0770778457020454E7));
        track.add(new GCS(-7.41519403629035E7,4.077072776890694E7));
        track.add(new GCS(-7.415203216712782E7,4.0770684150583446E7));
        track.add(new GCS(-7.415212342831363E7,4.077063272472237E7));
        track.add(new GCS(-7.415221466053776E7,4.077060836412237E7));
        track.add(new GCS(-7.415230549512003E7,4.077064927804428E7));
        track.add(new GCS(-7.415239607414234E7,4.077067976781671E7));
        track.add(new GCS(-7.41524810096712E7,4.077072051341752E7));
        track.add(new GCS(-7.415258102623716E7,4.077075106928261E7));
        track.add(new GCS(-7.415267333247727E7,4.077079815199758E7));
        track.add(new GCS(-7.415268657847251E7,4.077079911616161E7));

        var roadLog = dumbTracker.matchTrackToGraph(track);
        var expectedLog = List.of(
                new GraphRoad(new GraphNode(31, null), new GraphNode(48, null)),
                new GraphRoad(new GraphNode(48, null), new GraphNode(74, null)),
                new GraphRoad(new GraphNode(74, null), new GraphNode(72, null))

        );

        assertEquals(expectedLog, roadLog);
    }
}
