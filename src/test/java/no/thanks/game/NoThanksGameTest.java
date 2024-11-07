package no.thanks.game;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NoThanksGameTest {

    @Test
    void getPlayers_alwaysReturnsCorrectOrderFromStartingPlayer() {
        LinkedList<AbstractNoThanksPlayer> mockPlayers = new LinkedList<>();
        IntStream.range(0, 5).forEach(i -> {
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(false);
            when(mockPlayer.getId()).thenReturn(String.valueOf(i));
            mockPlayers.add(mockPlayer);
        });
        NoThanksGame game = new NoThanksGame(mockPlayers.toArray(new AbstractNoThanksPlayer[5]));
        assertEquals(mockPlayers.stream().map(AbstractNoThanksPlayer::getId).toList(), game.getPlayers());

        IntStream.range(0, 5).forEach(i -> {
            mockPlayers.add(mockPlayers.poll());
            game.offerCard(new NoThanksGame.CardOffer(0, 0));
            final List<String> expected = mockPlayers.stream().map(AbstractNoThanksPlayer::getId).toList();
            System.out.println("New expected order: " + expected);
            assertEquals(expected, game.getPlayers());
        });
    }

    @Nested
    class Play {

        @Test
        void deckOfTwentyFour() {
            // 24 = 35 - 3 - 9
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(true);
            NoThanksGame game = new NoThanksGame(mockPlayer);

            game.play();

            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(mockPlayer, times(24)).offer(captor.capture(), eq(0));
        }

        @RepeatedTest(50)
        void CardsAreInExpectedRange() {
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(true);
            NoThanksGame game = new NoThanksGame(mockPlayer);

            game.play();

            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(mockPlayer, times(24)).offer(captor.capture(), eq(0));
            assertTrue(captor.getAllValues().stream().max(Comparator.naturalOrder()).get() <= 35, "Not Played Above 35");
            assertTrue(captor.getAllValues().stream().min(Comparator.naturalOrder()).get() >= 3, "Not Played Below 3");
        }

        @RepeatedTest(50)
        void singlePlayerScoreIsExpected() {
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            AbstractNoThanksPlayer mockPlayerB = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(true);
            when(mockPlayer.getId()).thenReturn("mock1");
            when(mockPlayerB.getId()).thenReturn("mock2");
            NoThanksGame game = new NoThanksGame(mockPlayer, mockPlayerB);

            game.play();

            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(mockPlayer, times(24)).offer(captor.capture(), eq(0));
            System.out.println(game.getScore().get("mock1"));
            //Figure out a good way to score player 1 or swap to passing in args
//            assertEquals(captor.getAllValues().stream().mapToInt(i -> i).sum() -11, game.getScore().get(mockPlayer));
            assertEquals(-11, game.getScore().get("mock2"));
        }

        @Test
        void playersAreForcedToTakeIfTheyHaveNoCounters() {
            // 288 = 24 *12
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(false);
            NoThanksGame game = new NoThanksGame(mockPlayer);

            game.play();

            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            verify(mockPlayer, times(288)).offer(captor.capture(), anyInt());
        }

        @Test
        void playersAreForcedToTakeIfTheyHaveNoCounters_Two() {
            // 288 = 24 *12
            AbstractNoThanksPlayer mockPlayer = mock(AbstractNoThanksPlayer.class);
            when(mockPlayer.offer(anyInt(), anyInt())).thenReturn(false);
            AbstractNoThanksPlayer mockPlayerB = mock(AbstractNoThanksPlayer.class);
            when(mockPlayerB.offer(anyInt(), anyInt())).thenReturn(false);
            NoThanksGame game = new NoThanksGame(mockPlayer, mockPlayerB);

            game.play();

            ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            game.getScore();
            System.out.println(game.cards());
            verify(mockPlayer, times(144)).offer(captor.capture(), anyInt());
            verify(mockPlayerB, times(148)).offer(captor.capture(), anyInt());
        }
    }
}